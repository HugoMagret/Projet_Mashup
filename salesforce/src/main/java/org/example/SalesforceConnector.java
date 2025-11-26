package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Connecteur Salesforce qui gère automatiquement l'authentification
 * et la régénération du token en cas d'expiration.
 */
public class SalesforceConnector {

    private final SalesforceAuthenticator authenticator;
    private final String staticAccessToken;
    private final String staticInstanceUrl;
    private final String apiVersion = "v59.0";
    private final HttpClient httpClient;
    private final Gson gson;
    private final boolean useAuthenticator;

    /**
     * Constructeur avec authentificateur (recommandé)
     * Le token sera régénéré automatiquement en cas d'expiration
     */
    public SalesforceConnector(SalesforceAuthenticator authenticator) {
        this.authenticator = authenticator;
        this.staticAccessToken = null;
        this.staticInstanceUrl = null;
        this.useAuthenticator = true;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Constructeur avec token statique (pour compatibilité)
     * @deprecated Utilisez SalesforceAuthenticator pour la régénération automatique
     */
    @Deprecated
    public SalesforceConnector(String accessToken, String instanceUrl) {
        this.authenticator = null;
        this.staticAccessToken = accessToken;
        this.staticInstanceUrl = instanceUrl;
        this.useAuthenticator = false;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public String executeQuery(String soqlQuery) throws IOException, InterruptedException {
        int maxRetries = 2;
        int attempt = 0;
        
        while (attempt < maxRetries) {
            try {
                String encodedQuery = URLEncoder.encode(soqlQuery, StandardCharsets.UTF_8);
                String instanceUrl = useAuthenticator ? authenticator.getInstanceUrl() : staticInstanceUrl;
                String uriStr = instanceUrl + "/services/data/" + apiVersion + "/query/?q=" + encodedQuery;
                String accessToken = useAuthenticator ? authenticator.getAccessToken() : staticAccessToken;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(uriStr))
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Si le token a expiré (401), régénérer et réessayer
                if (response.statusCode() == 401 && useAuthenticator && attempt < maxRetries - 1) {
                    authenticator.refreshToken();
                    attempt++;
                    continue;
                }

                if (response.statusCode() != 200) {
                    throw new IOException(
                            "Failed to execute query. Status code: " + response.statusCode() + ", Body: " + response.body());
                }

                // Pretty print the JSON
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                return gson.toJson(jsonObject);
                
            } catch (Exception e) {
                // Si c'est une erreur de token et qu'on peut réessayer, continuer
                if (useAuthenticator && attempt < maxRetries - 1) {
                    attempt++;
                    continue;
                }
                throw e;
            }
        }
        
        throw new IOException("Échec après " + maxRetries + " tentatives");
    }
}
