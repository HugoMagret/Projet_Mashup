package org.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Vector;

/**
 * Client graphique pour VirtualCRMService.
 *
 * Deux types de recherche :
 *  1) Par revenus : /api/leads?minRevenue=...&maxRevenue=...&province=...
 *  2) Par dates  : /api/leads/byDate?startDate=...&endDate=...
 *
 * Les résultats (VirtualLeadDTO) sont affichés dans un tableau :
 *  - nom, prénom
 *  - entreprise
 *  - revenu annuel
 *  - téléphone
 *  - rue, code postal, ville, département, pays
 *  - date de création
 *  - latitude / longitude (si géolocalisation disponible)
 */
public class ClientApp extends JFrame {

    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Champ pour la base URL
    private final JTextField tfBaseUrl = new JTextField(DEFAULT_BASE_URL);

    // Recherche par revenus
    private final JTextField tfMinRevenue = new JTextField("50000");
    private final JTextField tfMaxRevenue = new JTextField("150000");
    private final JTextField tfProvince   = new JTextField("");
    private final JButton btnSearchRevenue = new JButton("Rechercher (revenus)");

    // Recherche par dates
    private final JTextField tfStartDate = new JTextField("2024-01-01T00:00:00Z");
    private final JTextField tfEndDate   = new JTextField("2024-12-31T23:59:59Z");
    private final JButton btnSearchDate = new JButton("Rechercher (dates)");

    // Tableau de résultats
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{
                    "Nom", "Prénom", "Entreprise",
                    "Revenu (€)", "Téléphone",
                    "Rue", "Code postal", "Ville", "Département", "Pays",
                    "Date création",
                    "Latitude", "Longitude"
            },
            0
    );
    private final JTable table = new JTable(tableModel);

    public ClientApp() {
        super("VirtualCRM - Client graphique");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));

        // ---------- Bandeau supérieur : Base URL ----------
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(new JLabel("Base URL VirtualCRM : "), BorderLayout.WEST);
        topPanel.add(tfBaseUrl, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ---------- Centre haut : deux panneaux de critères ----------
        JPanel criteriaPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        criteriaPanel.add(buildRevenuePanel());
        criteriaPanel.add(buildDatePanel());
        add(criteriaPanel, BorderLayout.CENTER);

        // ---------- Bas : tableau de résultats ----------
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        // Actions des boutons
        btnSearchRevenue.addActionListener(e -> searchByRevenue());
        btnSearchDate.addActionListener(e -> searchByDate());

        pack();
        setSize(1100, 600);
        setLocationRelativeTo(null);
    }

    // Construit le panneau "Recherche par revenus"
    private JPanel buildRevenuePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Recherche par revenus"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        // Revenu min
        c.gridy = row; c.gridx = 0; c.weightx = 0;
        p.add(new JLabel("Revenu min (€) :"), c);
        c.gridx = 1; c.weightx = 1.0;
        p.add(tfMinRevenue, c);
        row++;

        // Revenu max
        c.gridy = row; c.gridx = 0; c.weightx = 0;
        p.add(new JLabel("Revenu max (€) :"), c);
        c.gridx = 1; c.weightx = 1.0;
        p.add(tfMaxRevenue, c);
        row++;

        // Province
        c.gridy = row; c.gridx = 0; c.weightx = 0;
        p.add(new JLabel("Province / département :"), c);
        c.gridx = 1; c.weightx = 1.0;
        p.add(tfProvince, c);
        row++;

        // Bouton
        c.gridy = row; c.gridx = 1; c.weightx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        p.add(btnSearchRevenue, c);

        return p;
    }

    // Construit le panneau "Recherche par dates"
    private JPanel buildDatePanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Recherche par dates"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        JLabel lblFormat = new JLabel("<html><i>Format : yyyy-MM-dd'T'HH:mm:ss'Z'</i></html>");
        c.gridy = row; c.gridx = 0; c.gridwidth = 2; c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        p.add(lblFormat, c);
        row++;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.LINE_END;

        // Date début
        c.gridy = row; c.gridx = 0; c.weightx = 0;
        p.add(new JLabel("Date début :"), c);
        c.gridx = 1; c.weightx = 1.0;
        p.add(tfStartDate, c);
        row++;

        // Date fin
        c.gridy = row; c.gridx = 0; c.weightx = 0;
        p.add(new JLabel("Date fin :"), c);
        c.gridx = 1; c.weightx = 1.0;
        p.add(tfEndDate, c);
        row++;

        // Bouton
        c.gridy = row; c.gridx = 1; c.weightx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        p.add(btnSearchDate, c);

        return p;
    }

    // ---------- Logique de recherche par revenus ----------

    private void searchByRevenue() {
        btnSearchRevenue.setEnabled(false);
        btnSearchDate.setEnabled(false);
        tableModel.setRowCount(0);

        final String baseUrl   = tfBaseUrl.getText().trim().replaceAll("/+$", "");
        final String minStr    = tfMinRevenue.getText().trim();
        final String maxStr    = tfMaxRevenue.getText().trim();
        final String province  = tfProvince.getText().trim();

        double minRevenue;
        double maxRevenue;
        try {
            minRevenue = Double.parseDouble(minStr);
            maxRevenue = Double.parseDouble(maxStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Les revenus doivent être des nombres (ex: 10000, 50000).",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            btnSearchRevenue.setEnabled(true);
            btnSearchDate.setEnabled(true);
            return;
        }

        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                String encodedProvince = URLEncoder.encode(province == null ? "" : province, StandardCharsets.UTF_8);

                String url = String.format(Locale.ROOT,
                        "%s/api/leads?minRevenue=%f&maxRevenue=%f&province=%s",
                        baseUrl, minRevenue, maxRevenue, encodedProvince);

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

            @Override
            protected void done() {
                try {
                    JsonNode leads = get();
                    fillTableWithLeads(leads, "Aucun Lead trouvé pour ces critères (revenus).");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClientApp.this,
                            "Erreur lors de l'appel au service VirtualCRM : " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSearchRevenue.setEnabled(true);
                    btnSearchDate.setEnabled(true);
                }
            }
        }.execute();
    }

    // ---------- Logique de recherche par dates ----------

    private void searchByDate() {
        btnSearchRevenue.setEnabled(false);
        btnSearchDate.setEnabled(false);
        tableModel.setRowCount(0);

        final String baseUrl   = tfBaseUrl.getText().trim().replaceAll("/+$", "");
        final String startDate = tfStartDate.getText().trim();
        final String endDate   = tfEndDate.getText().trim();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Les deux dates doivent être renseignées.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            btnSearchRevenue.setEnabled(true);
            btnSearchDate.setEnabled(true);
            return;
        }

        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                String encodedStart = URLEncoder.encode(startDate, StandardCharsets.UTF_8);
                String encodedEnd   = URLEncoder.encode(endDate,   StandardCharsets.UTF_8);

                String url = String.format("%s/api/leads/byDate?startDate=%s&endDate=%s",
                        baseUrl, encodedStart, encodedEnd);

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

            @Override
            protected void done() {
                try {
                    JsonNode leads = get();
                    fillTableWithLeads(leads, "Aucun Lead trouvé pour ces critères (dates).");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClientApp.this,
                            "Erreur lors de l'appel au service VirtualCRM : " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSearchRevenue.setEnabled(true);
                    btnSearchDate.setEnabled(true);
                }
            }
        }.execute();
    }

    // ---------- Remplissage du tableau à partir du JSON ----------

    private void fillTableWithLeads(JsonNode leads, String emptyMessage) {
        if (leads == null || !leads.isArray() || leads.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    emptyMessage,
                    "Résultat", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

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
            String latStr = "-";
            String lonStr = "-";
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
                    : String.format(localeFr, "%.2f", annualRevenue);

            Vector<Object> row = new Vector<>();
            row.add(lastName);
            row.add(firstName);
            row.add(companyName);
            row.add(revenueStr);
            row.add(phone);
            row.add(street);
            row.add(postalCode);
            row.add(city);
            row.add(state);
            row.add(country);
            row.add(creationDate);
            row.add(latStr);
            row.add(lonStr);

            tableModel.addRow(row);
        }
    }

    // ---------- main() : démarrage de l'interface graphique ----------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientApp().setVisible(true));
    }
}
