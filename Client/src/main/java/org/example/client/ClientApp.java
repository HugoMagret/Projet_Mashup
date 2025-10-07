package org.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ClientApp est une application en ligne de commande permettant
 * d'interroger le service REST VirtualCRM.
 * Elle envoie une requête HTTP contenant les paramètres de recherche,
 * récupère la liste des leads (clients potentiels) et les affiche dans la console.
 */
public class ClientApp {

    // Objet utilisé pour parser les données JSON reçues du service REST
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Point d'entrée principal de l'application.
     * @param args Trois arguments sont attendus :
     *             revenueMin, revenueMax, province
     */
    public static void main(String[] args) {

        // Vérification des arguments
        if (args.length < 3) {
            System.err.println("Usage: ClientApp <revenueMin> <revenueMax> <province>");
            System.exit(1);
        }

        // Lecture des paramètres
        String revenueMin = args[0];
        String revenueMax = args[1];
        String province   = args[2];

        // Construction de l'URL de la requête HTTP vers le service REST
        String url = String.format(
                "http://localhost:8080/virtualcrm/findLeads?revenueMin=%s&revenueMax=%s&province=%s",
                revenueMin, revenueMax, province);

        // Création et utilisation d'un client HTTP (sera automatiquement fermé après le try)
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            // Exécution de la requête HTTP
            // On utilise ici un ResponseHandler (API moderne) pour éviter les warnings de dépréciation.
            // Ce handler renvoie directement le corps de la réponse (JSON sous forme de chaîne).
            String responseBody = client.execute(request, httpResponse -> {
                if (httpResponse.getCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("Erreur HTTP " + httpResponse.getCode() +
                            " : " + httpResponse.getReasonPhrase());
                }
                return EntityUtils.toString(httpResponse.getEntity());
            });

            // Lecture du corps de la réponse JSON
            JsonNode leads = mapper.readTree(responseBody);

            // Vérifie que le JSON contient bien un tableau de résultats
            if (!leads.isArray() || leads.isEmpty()) {
                System.out.println("Aucun lead trouvé pour ces critères.");
                return;
            }

            // Prépare une liste de lignes à afficher sous forme de tableau texte
            List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{"Nom", "Prénom", "Société", "Revenu (€)"});

            // Pour chaque lead du tableau JSON, on extrait les informations nécessaires
            for (JsonNode lead : leads) {
                String firstName = lead.path("firstName").asText("-");
                String lastName  = lead.path("lastName").asText("-");
                String company   = lead.path("company").asText("-");
                String revenue   = lead.path("annualRevenue").asText("-");

                rows.add(new String[]{lastName, firstName, company, revenue});
            }

            // Affiche les résultats sous forme de tableau formaté
            printTable(rows);

        } catch (Exception e) {
            // Gestion générique des exceptions (erreur réseau, JSON invalide, etc.)
            System.err.println("Erreur lors de la requête : " + e.getMessage());
        }
    }

    /**
     * Affiche une liste de lignes sous forme de tableau aligné dans la console.
     * Chaque sous-tableau correspond à une ligne.
     * @param rows lignes du tableau
     */
    private static void printTable(List<String[]> rows) {
        int[] colWidths = new int[rows.get(0).length];

        // Calcule la largeur maximale de chaque colonne
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                colWidths[i] = Math.max(colWidths[i], row[i].length());
            }
        }

        // Affiche les lignes avec alignement correct
        for (int r = 0; r < rows.size(); r++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rows.get(r).length; i++) {
                sb.append(padRight(rows.get(r)[i], colWidths[i] + 2));
            }
            System.out.println(sb);

            // Affiche une ligne de séparation après l'en-tête
            if (r == 0) {
                for (int width : colWidths) {
                    System.out.print("-".repeat(width + 2));
                }
                System.out.println();
            }
        }
    }

    /**
     * Ajoute des espaces à droite d'une chaîne pour obtenir une longueur fixe.
     * Utilisé pour aligner les colonnes dans le tableau.
     * @param text   texte à ajuster
     * @param length longueur totale souhaitée
     * @return texte complété par des espaces
     */
    private static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }
}
