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
 * Client en ligne de commande pour VirtualCRMService.
 *
 * Deux modes :
 *  1) Recherche par revenus : /api/leads?minRevenue=...&maxRevenue=...&province=...
 *  2) Recherche par dates  : /api/leads/byDate?startDate=...&endDate=...
 *
 * Le service renvoie une liste de VirtualLeadDTO au format JSON.
 * Ce client affiche toutes les informations demandées dans l'énoncé :
 *  - nom, prénom
 *  - entreprise
 *  - revenu annuel
 *  - téléphone
 *  - adresse complète (rue, code postal, ville, département, pays)
 *  - date de création
 *  - position géographique (latitude / longitude) si disponible
 */
public final class ClientApp {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ClientApp() {
        // pas d'instance
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1) Base URL
        System.out.println("=== Client VirtualCRM ===");
        System.out.print("Base URL du service [" + DEFAULT_BASE_URL + "] : ");
        String baseInput = scanner.nextLine().trim();
        String baseUrl = baseInput.isEmpty() ? DEFAULT_BASE_URL : baseInput;

        // 2) Choix du mode
        System.out.println();
        System.out.println("Choisissez le type de recherche :");
        System.out.println("  1) Par revenus (min / max / province)");
        System.out.println("  2) Par dates (startDate / endDate, ISO-8601)");
        System.out.print("Votre choix (1/2) : ");
        String choice = scanner.nextLine().trim();

        try {
            if ("1".equals(choice)) {
                runSearchByRevenue(scanner, baseUrl);
            } else if ("2".equals(choice)) {
                runSearchByDate(scanner, baseUrl);
            } else {
                System.out.println("Choix invalide, arrêt du client.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel au service VirtualCRM : " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Mode 1 : recherche par revenus
    // -------------------------------------------------------------------------

    private static void runSearchByRevenue(Scanner scanner, String baseUrl) throws Exception {
        System.out.println();
        System.out.println("=== Recherche par revenus ===");

        System.out.print("Revenu annuel minimum : ");
        String minStr = scanner.nextLine().trim();

        System.out.print("Revenu annuel maximum : ");
        String maxStr = scanner.nextLine().trim();

        double minRevenue;
        double maxRevenue;
        try {
            minRevenue = Double.parseDouble(minStr);
            maxRevenue = Double.parseDouble(maxStr);
        } catch (NumberFormatException e) {
            System.err.println("Les revenus doivent être des nombres (ex: 10000, 50000).");
            return;
        }

        System.out.print("Province / département (optionnel, Enter = toutes) : ");
        String province = scanner.nextLine().trim();

        JsonNode leads = callFindLeads(baseUrl, minRevenue, maxRevenue, province);

        if (leads == null || !leads.isArray() || leads.isEmpty()) {
            System.out.println();
            System.out.println("Aucun Lead trouvé pour ces critères.");
            return;
        }

        System.out.println();
        System.out.println("=== Résultats (par revenus) ===");
        printLeads(leads);
    }

    private static JsonNode callFindLeads(String baseUrl,
                                          double minRevenue,
                                          double maxRevenue,
                                          String province) throws Exception {

        String trimmedBase = baseUrl.trim().replaceAll("/+$", "");
        String encodedProvince = URLEncoder.encode(province == null ? "" : province, StandardCharsets.UTF_8);

        // Paramètres compatibles avec VirtualCRMController :
        // @RequestParam double minRevenue, @RequestParam double maxRevenue, @RequestParam String province
        String url = String.format(Locale.ROOT,
                "%s/api/leads?minRevenue=%f&maxRevenue=%f&province=%s",
                trimmedBase, minRevenue, maxRevenue, encodedProvince);

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

    // -------------------------------------------------------------------------
    // Mode 2 : recherche par dates
    // -------------------------------------------------------------------------

    private static void runSearchByDate(Scanner scanner, String baseUrl) throws Exception {
        System.out.println();
        System.out.println("=== Recherche par dates ===");
        System.out.println("Format attendu (ISO-8601, UTC) : yyyy-MM-dd'T'HH:mm:ss'Z'");
        System.out.println("Exemple : 2024-01-01T00:00:00Z");

        System.out.print("Date de début (startDate) : ");
        String startDate = scanner.nextLine().trim();

        System.out.print("Date de fin   (endDate)   : ");
        String endDate = scanner.nextLine().trim();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            System.err.println("Les deux dates doivent être renseignées.");
            return;
        }

        JsonNode leads = callFindLeadsByDate(baseUrl, startDate, endDate);

        if (leads == null || !leads.isArray() || leads.isEmpty()) {
            System.out.println();
            System.out.println("Aucun Lead trouvé pour ces critères.");
            return;
        }

        System.out.println();
        System.out.println("=== Résultats (par dates) ===");
        printLeads(leads);
    }

    private static JsonNode callFindLeadsByDate(String baseUrl,
                                                String startDate,
                                                String endDate) throws Exception {

        String trimmedBase = baseUrl.trim().replaceAll("/+$", "");
        String encodedStart = URLEncoder.encode(startDate, StandardCharsets.UTF_8);
        String encodedEnd   = URLEncoder.encode(endDate,   StandardCharsets.UTF_8);

        // Compatible avec VirtualCRMController :
        // @GetMapping("/byDate")
        // @RequestParam String startDate, @RequestParam String endDate
        String url = String.format("%s/api/leads/byDate?startDate=%s&endDate=%s",
                trimmedBase, encodedStart, encodedEnd);

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

    // -------------------------------------------------------------------------
    // Affichage commun des résultats (VirtualLeadDTO)
    // -------------------------------------------------------------------------

    /**
     * Affiche les VirtualLeadDTO (sous forme de JsonNode) renvoyés par le service.
     *
     * Champs attendus :
     *  - firstName, lastName
     *  - companyName
     *  - annualRevenue
     *  - phone
     *  - street, postalCode, city, state, country
     *  - creationDate
     *  - geographicPoint { latitude, longitude } (peut être nul)
     */
    private static void printLeads(JsonNode leads) {
        Locale localeFr = Locale.FRANCE;

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

            JsonNode geo = lead.path("geographicPoint");
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
