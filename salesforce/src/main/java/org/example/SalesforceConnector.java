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

public class SalesforceConnector {

    private final String accessToken;
    private final String instanceUrl;
    private final String apiVersion = "v59.0";
    private final HttpClient httpClient;
    private final Gson gson;

    public SalesforceConnector(String accessToken, String instanceUrl) {
        this.accessToken = accessToken;
        this.instanceUrl = instanceUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public String executeQuery(String soqlQuery) throws IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(soqlQuery, StandardCharsets.UTF_8);
        String uriStr = instanceUrl + "/services/data/" + apiVersion + "/query/?q=" + encodedQuery;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriStr))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Failed to execute query. Status code: " + response.statusCode() + ", Body: " + response.body());
        }

        // Pretty print the JSON
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        return gson.toJson(jsonObject);
    }
}
