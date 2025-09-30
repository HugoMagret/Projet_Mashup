package org.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class ClientApp {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: ClientApp <revenueMin> <revenueMax> <province>");
            System.exit(1);
        }

        String revenueMin = args[0];
        String revenueMax = args[1];
        String province   = args[2];

        String url = String.format(
                "http://localhost:8080/virtualcrm/findLeads?revenueMin=%s&revenueMax=%s&province=%s",
                revenueMin, revenueMax, province);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(request);

            if (response.getCode() != HttpStatus.SC_OK) {
                System.err.printf("Erreur HTTP %d : %s%n",
                        response.getCode(),
                        response.getReasonPhrase());
                System.exit(1);
            }

            String responseBody = EntityUtils.toString(response.getEntity());
            JsonNode leads = mapper.readTree(responseBody);

            if (!leads.isArray() || leads.isEmpty()) {
                System.out.println("Aucun lead trouvé pour ces critères.");
                return;
            }

            List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{"Nom", "Prénom", "Société", "Revenu (€)"});

            for (JsonNode lead : leads) {
                String firstName = lead.path("firstName").asText("-");
                String lastName  = lead.path("lastName").asText("-");
                String company   = lead.path("company").asText("-");
                String revenue   = lead.path("annualRevenue").asText("-");

                rows.add(new String[]{lastName, firstName, company, revenue});
            }

            printTable(rows);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la requête : " + e.getMessage());
        }
    }

    /** Affiche une liste de lignes sous forme de tableau formaté */
    private static void printTable(List<String[]> rows) {
        int[] colWidths = new int[rows.get(0).length];

        // calcul largeur max de chaque colonne
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                colWidths[i] = Math.max(colWidths[i], row[i].length());
            }
        }

        // afficher les lignes
        for (int r = 0; r < rows.size(); r++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rows.get(r).length; i++) {
                sb.append(padRight(rows.get(r)[i], colWidths[i] + 2));
            }
            System.out.println(sb);
            if (r == 0) { // séparateur après en-tête
                for (int width : colWidths) {
                    System.out.print("-".repeat(width + 2));
                }
                System.out.println();
            }
        }
    }

    private static String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }
}
