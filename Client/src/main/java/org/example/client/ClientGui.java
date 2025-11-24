package org.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;
import java.util.Vector;

public class ClientGui extends JFrame {

    // URL fixe de ton VirtualCRM (à adapter si besoin)
    private static final String BASE_URL = "http://localhost:8080";

    // Champs de recherche (tu peux en ajouter)
    private final JTextField tfFirstName   = new JTextField();
    private final JTextField tfLastName    = new JTextField();
    private final JTextField tfCompanyName = new JTextField();
    private final JTextField tfRevMin      = new JTextField("10000");
    private final JTextField tfRevMax      = new JTextField("50000");
    private final JTextField tfCity        = new JTextField("Angers");
    private final JTextField tfState       = new JTextField("Maine-et-Loire");
    private final JTextField tfCountry     = new JTextField("France");

    private final JButton btnSearch = new JButton("Rechercher");

    // On prévoit directement les colonnes latitude/longitude
    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                            "Nom", "Prénom", "Société",
                            "Revenu (€)", "Ville", "État/Province", "Pays",
                            "Latitude", "Longitude"
                    },
                    0
            );

    private final JTable table = new JTable(tableModel);

    private static final ObjectMapper mapper = new ObjectMapper();

    public ClientGui() {
        super("VirtualCRM - Client GUI");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // -------- Barre de paramètres (formulaire) ----------
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        // Ligne 1 : Prénom
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; c.weightx = 0;
        form.add(new JLabel("Prénom :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 1.0;
        form.add(tfFirstName, c);
        row++;

        // Ligne 2 : Nom
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; c.weightx = 0;
        form.add(new JLabel("Nom :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 1.0;
        form.add(tfLastName, c);
        row++;

        // Ligne 3 : Société
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; c.weightx = 0;
        form.add(new JLabel("Société :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 1.0;
        form.add(tfCompanyName, c);
        row++;

        // Ligne 4 : Revenu min
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; c.weightx = 0;
        form.add(new JLabel("Revenu min :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 1.0;
        form.add(tfRevMin, c);
        row++;

        // Ligne 5 : Revenu max
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; c.weightx = 0;
        form.add(new JLabel("Revenu max :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 1.0;
        form.add(tfRevMax, c);
        row++;

        // Ligne 6 : Ville
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; c.weightx = 0;
        form.add(new JLabel("Ville :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 1.0;
        form.add(tfCity, c);
        row++;

        // Ligne 7 : État/Province
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; c.weightx = 0;
        form.add(new JLabel("État / Province :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 1.0;
        form.add(tfState, c);
        row++;

        // Ligne 8 : Pays
        c.gridy = row; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; c.weightx = 0;
        form.add(new JLabel("Pays :"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 1.0;
        form.add(tfCountry, c);
        row++;

        // Ligne 9 : bouton Rechercher
        c.gridy = row; c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        form.add(btnSearch, c);

        add(form, BorderLayout.NORTH);

        // -------- Tableau de résultats ----------
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Action bouton
        btnSearch.addActionListener(e -> runQuery());

        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    private void runQuery() {
        btnSearch.setEnabled(false);
        tableModel.setRowCount(0);

        final String firstName   = tfFirstName.getText().trim();
        final String lastName    = tfLastName.getText().trim();
        final String companyName = tfCompanyName.getText().trim();
        final String revenueMin  = tfRevMin.getText().trim();
        final String revenueMax  = tfRevMax.getText().trim();
        final String city        = tfCity.getText().trim();
        final String state       = tfState.getText().trim();
        final String country     = tfCountry.getText().trim();

        // On accepte que tout soit vide : dans ce cas Virtual retournera "tous les leads" ou appliquera sa logique
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                // Construction de l’objet JSON de critères
                ObjectNode criteria = mapper.createObjectNode();

                if (!firstName.isEmpty())   criteria.put("firstName", firstName);
                if (!lastName.isEmpty())    criteria.put("lastName", lastName);
                if (!companyName.isEmpty()) criteria.put("companyName", companyName);
                if (!city.isEmpty())        criteria.put("city", city);
                if (!state.isEmpty())       criteria.put("state", state);
                if (!country.isEmpty())     criteria.put("country", country);

                // Revenus : on essaie de les parser si non vides
                if (!revenueMin.isEmpty()) {
                    try {
                        criteria.put("revenueMin", Double.parseDouble(revenueMin));
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Revenu min invalide : " + revenueMin);
                    }
                }
                if (!revenueMax.isEmpty()) {
                    try {
                        criteria.put("revenueMax", Double.parseDouble(revenueMax));
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Revenu max invalide : " + revenueMax);
                    }
                }

                String baseUrl = BASE_URL + "/api/leads";
                StringBuilder urlBuilder = new StringBuilder(baseUrl);
                urlBuilder.append("?minRevenue=").append(revenueMin.isEmpty() ? "0" : revenueMin);
                urlBuilder.append("&maxRevenue=").append(revenueMax.isEmpty() ? "1000000000" : revenueMax);
                urlBuilder.append("&province=").append(state.isEmpty() ? "" : java.net.URLEncoder.encode(state, java.nio.charset.StandardCharsets.UTF_8));

                String url = urlBuilder.toString();

                try (CloseableHttpClient http = HttpClients.createDefault()) {
                    org.apache.hc.client5.http.classic.methods.HttpGet request = new org.apache.hc.client5.http.classic.methods.HttpGet(url);
                    request.setHeader("Accept", "application/json");

                    String responseBody = http.execute(request, httpResponse -> {
                        if (httpResponse.getCode() != HttpStatus.SC_OK) {
                            throw new RuntimeException("Erreur HTTP " + httpResponse.getCode() + " : " +
                                    httpResponse.getReasonPhrase());
                        }
                        return EntityUtils.toString(httpResponse.getEntity());
                    });

                    return mapper.readTree(responseBody);
                }
            }

            @Override
            protected void done() {
                try {
                    JsonNode leads = get();
                    if (leads == null || !leads.isArray() || leads.isEmpty()) {
                        JOptionPane.showMessageDialog(ClientGui.this,
                                "Aucun lead trouvé pour ces critères.",
                                "Résultat", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    Locale localeFr = Locale.FRANCE;

                    for (JsonNode lead : leads) {
                        String firstName   = lead.path("firstName").asText("-");
                        String lastName    = lead.path("lastName").asText("-");
                        String companyName = lead.path("companyName").asText("-");
                        double revenueVal  = lead.path("annualRevenue").asDouble(0.0);
                        String city        = lead.path("city").asText("-");
                        String state       = lead.path("state").asText("-");
                        String country     = lead.path("country").asText("-");

                        double latVal = lead.path("latitude").asDouble(Double.NaN);
                        double lonVal = lead.path("longitude").asDouble(Double.NaN);

                        String revenueStr = (revenueVal <= 0.0)
                                ? "-"
                                : String.format(localeFr, "%.2f", revenueVal);

                        String latStr = Double.isNaN(latVal) ? "-" : String.format(localeFr, "%.6f", latVal);
                        String lonStr = Double.isNaN(lonVal) ? "-" : String.format(localeFr, "%.6f", lonVal);

                        Vector<Object> row = new Vector<>();
                        row.add(lastName);
                        row.add(firstName);
                        row.add(companyName);
                        row.add(revenueStr);
                        row.add(city);
                        row.add(state);
                        row.add(country);
                        row.add(latStr);
                        row.add(lonStr);

                        tableModel.addRow(row);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClientGui.this,
                            "Erreur : " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGui().setVisible(true));
    }
}
