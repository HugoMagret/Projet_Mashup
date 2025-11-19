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
import java.util.Vector;

public class ClientGui extends JFrame {

    private final JTextField tfBaseUrl = new JTextField("http://localhost:8080");
    private final JTextField tfRevMin  = new JTextField("10000");
    private final JTextField tfRevMax  = new JTextField("50000");
    private final JTextField tfProv    = new JTextField("Maine-et-Loire");
    private final JButton btnSearch    = new JButton("Rechercher");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Nom", "Prénom", "Société", "Revenu (€)"}, 0);
    private final JTable table = new JTable(tableModel);

    private static final ObjectMapper mapper = new ObjectMapper();

    public ClientGui() {
        super("VirtualCRM - Client GUI");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        ((JComponent)getContentPane()).setBorder(new EmptyBorder(10,10,10,10));

        // Barre de paramètres
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.gridy = 0; c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; form.add(new JLabel("Base URL :"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1.0; form.add(tfBaseUrl, c);

        c.gridy = 1; c.gridx = 0; c.weightx = 0; c.fill = GridBagConstraints.NONE; form.add(new JLabel("Revenu min :"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1.0; form.add(tfRevMin, c);

        c.gridy = 2; c.gridx = 0; c.weightx = 0; c.fill = GridBagConstraints.NONE; form.add(new JLabel("Revenu max :"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1.0; form.add(tfRevMax, c);

        c.gridy = 3; c.gridx = 0; c.weightx = 0; c.fill = GridBagConstraints.NONE; form.add(new JLabel("Province :"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1.0; form.add(tfProv, c);

        c.gridy = 4; c.gridx = 1; c.fill = GridBagConstraints.NONE; c.weightx = 0; c.anchor = GridBagConstraints.LINE_START;
        form.add(btnSearch, c);

        add(form, BorderLayout.NORTH);

        // Tableau de résultats
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Action bouton
        btnSearch.addActionListener(e -> runQuery());

        setSize(700, 420);
        setLocationRelativeTo(null);
    }

    private void runQuery() {
        btnSearch.setEnabled(false);
        tableModel.setRowCount(0);

        final String baseUrl    = tfBaseUrl.getText().trim().replaceAll("/+$", "");
        final String revenueMin = tfRevMin.getText().trim();
        final String revenueMax = tfRevMax.getText().trim();
        final String province   = tfProv.getText().trim();

        // Validation très simple
        if (baseUrl.isEmpty() || revenueMin.isEmpty() || revenueMax.isEmpty() || province.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez renseigner tous les champs.",
                    "Champs manquants", JOptionPane.WARNING_MESSAGE);
            btnSearch.setEnabled(true);
            return;
        }

        // Requête réseau en arrière-plan
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                String url = String.format("%s/virtualcrm/findLeads?revenueMin=%s&revenueMax=%s&province=%s",
                        baseUrl, encode(revenueMin), encode(revenueMax), encode(province));

                try (CloseableHttpClient http = HttpClients.createDefault()) {
                    HttpGet request = new HttpGet(url);

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
                    // Remplir le tableau
                    for (JsonNode lead : leads) {
                        String firstName = lead.path("firstName").asText("-");
                        String lastName  = lead.path("lastName").asText("-");
                        String company   = lead.path("company").asText("-");
                        String revenue   = lead.path("annualRevenue").asText("-");
                        Vector<String> row = new Vector<>();
                        row.add(lastName);
                        row.add(firstName);
                        row.add(company);
                        row.add(revenue);
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

    // Encodage très simple pour l’URL (espace → %20, etc.)
    private static String encode(String s) {
        try {
            return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGui().setVisible(true));
    }
}
