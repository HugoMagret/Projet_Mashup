package org.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

/**
 * Simple client en ligne de commande pour VirtualCRMService.
 *
 * - Appelle l'opération REST /virtualcrm/findLeads
 *   avec les paramètres lowAnnualRevenue, highAnnualRevenue, province.
 * - Affiche toutes les informations de chaque VirtualLeadDto
 *   (y compris la géolocalisation si disponible).
 *
 * Le tri par profit potentiel (du plus grand au plus faible) doit être
 * réalisé côté service VirtualCRM, conformément au sujet.
 */
public final class ClientApp {

    // URL de base du VirtualCRM (à adapter si besoin)
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ClientApp() {
        // pas d'instance
    }

    public static void main(String[] args) {
        // 1) Récupération des paramètres
        double lowAnnualRevenue;
        double highAnnualRevenue;
        String province;
        String baseUrl = DEFAULT_BASE_URL;

        if (args.length == 3 || args.length == 4) {
            int index = 0;
            // Si le 1er argument ressemble à une URL, on le prend comme baseUrl
            if (args.length == 4) {
                baseUrl = args[0];
                index = 1;
            }
            try {
                lowAnnualRevenue  = Double.parseDouble(args[index]);
                highAnnualRevenue = Double.parseDouble(args[index + 1]);
            } catch (NumberFormatException e) {
                System.err.println("Les deux premiers arguments doivent être des nombres (lowAnnualRevenue highAnnualRevenue).");
                return;
            }
            province = args[index + 2];
        } else {
            // Pas le bon nombre d'arguments → on passe en mode interactif
            Scanner scanner = new Scanner(System.in);
            System.out.println("=== Client VirtualCRM (findLeads) ===");
            System.out.print("Base URL du service [" + DEFAULT_BASE_URL + "] : ");
            String baseInput = scanner.nextLine().trim();
            if (!baseInput.isEmpty()) {
                baseUrl = baseInput;
            }

            System.out.print("Revenu annuel minimum : ");
            lowAnnualRevenue = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Revenu annuel maximum : ");
            highAnnualRevenue = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Province (département) : ");
            province = scanner.nextLine().trim();

            if (province.isEmpty()) {
                System.err.println("La province ne doit pas être vide (conformément au sujet).");
                return;
            }
        }

        // 2) Appel REST
        try {
            JsonNode leads = callFindLeads(baseUrl, lowAnnualRevenue, highAnnualRevenue, province);

            if (leads == null || !leads.isArray() || leads.isEmpty()) {
                System.out.println("Aucun Lead trouvé pour ces critères.");
                return;
            }

            // 3) Affichage des résultats
            printLeads(leads);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel au service VirtualCRM : " + e.getMessage());
        }
    }

    /**
     * Appelle l'endpoint REST /virtualcrm/findLeads
     * avec les paramètres lowAnnualRevenue, highAnnualRevenue, province.
     */
    private static JsonNode callFindLeads(String baseUrl,
                                          double lowAnnualRevenue,
                                          double highAnnualRevenue,
                                          String province) throws Exception {

        String trimmedBase = baseUrl.trim().replaceAll("/+$", "");

        String encodedProvince = URLEncoder.encode(province, StandardCharsets.UTF_8);
        // ⚠️ Noms des paramètres adaptés au sujet : lowAnnualRevenue, highAnnualRevenue, province
        String url = String.format(Locale.ROOT,
                "%s/virtualcrm/findLeads?lowAnnualRevenue=%f&highAnnualRevenue=%f&province=%s",
                trimmedBase, lowAnnualRevenue, highAnnualRevenue, encodedProvince);

        try (CloseableHttpClient http = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            String responseBody = http.execute(request, httpResponse -> {
                if (httpResponse.getCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("Erreur HTTP " + httpResponse.getCode() + " : " +
                            httpResponse.getReasonPhrase());
                }
                return EntityUtils.toString(httpResponse.getEntity());
            });

            return MAPPER.readTree(responseBody);
        }
    }

    /**
     * Affiche chaque VirtualLeadDto renvoyé par le service.
     *
     * Champs attendus (adaptables selon ton JSON) :
     * - lastName
     * - firstName
     * - companyName
     * - annualRevenue
     * - phone
     * - street
     * - postalCode
     * - city
     * - state
     * - country
     * - creationDate
     * - geographicPointDto { latitude, longitude } (peut être nul)
     */
    private static void printLeads(JsonNode leads) {
        Locale localeFr = Locale.FRANCE;

        System.out.println("=== Résultats VirtualCRM (triés par profit potentiel décroissant) ===");
        for (JsonNode lead : leads) {
            String lastName      = lead.path("lastName").asText("-");
            String firstName     = lead.path("firstName").asText("-");
            String companyName   = lead.path("companyName").asText("-");
            double annualRevenue = lead.path("annualRevenue").asDouble(0.0);
            String phone         = lead.path("phone").asText("-");
            String street        = lead.path("street").asText("-");
            String postalCode    = lead.path("postalCode").asText("-");
            String city          = lead.path("city").asText("-");
            String state         = lead.path("state").asText("-");
            String country       = lead.path("country").asText("-");
            String creationDate  = lead.path("creationDate").asText("-");

            // Gestion de l'objet de géolocalisation (peut s'appeler geographicPointDto, geographicPointTO ou geographicPoint)
            JsonNode geo = lead.path("geographicPointDto");
            if (geo.isMissingNode() || geo.isNull()) {
                geo = lead.path("geographicPointTO");
            }
            if (geo.isMissingNode() || geo.isNull()) {
                geo = lead.path("geographicPoint");
            }

            String latStr = "inconnue";
            String lonStr = "inconnue";

            if (geo != null && !geo.isMissingNode() && !geo.isNull()) {
                double lat = geo.path("latitude").asDouble(Double.NaN);
                double lon = geo.path("longitude").asDouble(Double.NaN);

                if (!Double.isNaN(lat) && !Double.isNaN(lon)) {
                    latStr = String.format(localeFr, "%.6f", lat);
                    lonStr = String.format(localeFr, "%.6f", lon);
                }
            }

            String revenueStr = (annualRevenue <= 0.0)
                    ? "-"
                    : String.format(localeFr, "%.2f €", annualRevenue);

            System.out.println("------------------------------------------------------------");
            System.out.printf("Nom / Prénom : %s %s%n", lastName, firstName);
            System.out.printf("Entreprise   : %s%n", companyName);
            System.out.printf("Revenu annuel: %s%n", revenueStr);
            System.out.printf("Téléphone    : %s%n", phone);
            System.out.printf("Adresse      : %s%n", street);
            System.out.printf("Code postal  : %s%n", postalCode);
            System.out.printf("Ville        : %s%n", city);
            System.out.printf("Département  : %s%n", state);
            System.out.printf("Pays         : %s%n", country);
            System.out.printf("Date création: %s%n", creationDate);
            System.out.printf("Position     : latitude=%s, longitude=%s%n", latStr, lonStr);
        }
        System.out.println("------------------------------------------------------------");
    }
}
