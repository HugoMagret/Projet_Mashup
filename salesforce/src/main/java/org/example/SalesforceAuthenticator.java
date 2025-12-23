package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Gère l'authentification Salesforce via OAuth 2.0 Username-Password Flow
 * et régénère automatiquement le token lorsqu'il expire.
 */
public class SalesforceAuthenticator {
    
    private final String clientId;
    private final String clientSecret;
    private final String username;
    private final String password;
    private final String loginUrl;
    
    private String accessToken;
    private String instanceUrl;
    private Instant tokenExpiresAt;
    private final ReentrantLock lock = new ReentrantLock();
    private final HttpClient httpClient;
    
    /**
     * Crée un authentificateur Salesforce
     * 
     * @param clientId Client ID de l'application connectée Salesforce
     * @param clientSecret Client Secret de l'application connectée Salesforce
     * @param username Nom d'utilisateur Salesforce
     * @param password Mot de passe + Security Token (concaténés)
     * @param loginUrl URL de login (https://login.salesforce.com ou https://test.salesforce.com)
     */
    public SalesforceAuthenticator(String clientId, String clientSecret, 
                                   String username, String password, 
                                   String loginUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        this.password = password;
        this.loginUrl = loginUrl != null ? loginUrl : "https://login.salesforce.com";
        this.httpClient = HttpClient.newHttpClient();
    }
    
    /**
     * Obtient un token d'accès valide, en régénérant si nécessaire
     */
    public String getAccessToken() throws IOException, InterruptedException {
        lock.lock();
        try {
            // Vérifier si le token est valide (avec une marge de 5 minutes)
            if (accessToken != null && tokenExpiresAt != null && 
                Instant.now().plusSeconds(300).isBefore(tokenExpiresAt)) {
                return accessToken;
            }
            
            // Token expiré ou inexistant, régénérer
            authenticate();
            return accessToken;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Récupère l'URL de l'instance Salesforce
     */
    public String getInstanceUrl() throws IOException, InterruptedException {
        lock.lock();
        try {
            if (instanceUrl == null) {
                authenticate();
            }
            return instanceUrl;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Effectue l'authentification OAuth 2.0 Username-Password Flow
     */
    private void authenticate() throws IOException, InterruptedException {
        String requestBody = String.format(
            "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
            java.net.URLEncoder.encode(clientId, StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(clientSecret, StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(username, StandardCharsets.UTF_8),
            java.net.URLEncoder.encode(password, StandardCharsets.UTF_8)
        );
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(loginUrl + "/services/oauth2/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException(
                "Échec de l'authentification Salesforce. Code: " + response.statusCode() + 
                ", Réponse: " + response.body()
            );
        }
        
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        
        this.accessToken = jsonResponse.get("access_token").getAsString();
        this.instanceUrl = jsonResponse.get("instance_url").getAsString();
        
        // Calculer l'expiration (par défaut 2 heures, mais on utilise issued_at si disponible)
        long issuedAt = jsonResponse.has("issued_at") 
            ? Long.parseLong(jsonResponse.get("issued_at").getAsString()) 
            : Instant.now().getEpochSecond();
        
        // Les tokens Salesforce expirent généralement après 2 heures (7200 secondes)
        // On utilise une durée de vie de 2 heures par défaut
        this.tokenExpiresAt = Instant.ofEpochSecond(issuedAt).plusSeconds(7200);
    }
    
    /**
     * Force la régénération du token (utile pour les tests ou en cas d'erreur)
     */
    public void refreshToken() throws IOException, InterruptedException {
        lock.lock();
        try {
            this.accessToken = null;
            this.instanceUrl = null;
            this.tokenExpiresAt = null;
            authenticate();
        } finally {
            lock.unlock();
        }
    }
}

