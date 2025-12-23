package org.example.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.SalesforceAuthenticator;
import org.example.SalesforceConnector;
import org.example.SalesforceLeadDTO;
import org.example.dto.VirtualLeadDTO;
import org.example.util.LeadMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SalesforceClient {

    private final SalesforceConnector connector;
    private final SalesforceAuthenticator authenticator;

    public SalesforceClient() {
        String clientId = System.getenv("SF_CLIENT_ID");
        String clientSecret = System.getenv("SF_CLIENT_SECRET");
        String username = System.getenv("SF_USERNAME");
        String password = System.getenv("SF_PASSWORD");
        String securityToken = System.getenv("SF_TOKEN");
        String loginUrl = System.getenv("SALESFORCE_LOGIN_URL");
        
        // Si les variables d'environnement ne sont pas définies, utiliser les valeurs par défaut
        if (clientId == null) {
            clientId = "3MVG9dAEux2v1sLtbdQ7kVGBVRvnIT0JhqShfbp5Oao2IeKy71uEOygjkHYmwzP81u.M5ry_Ihi_x_DX3uOQJ";
        }
        if (clientSecret == null) {
            clientSecret = "4CA14F77B4FCAF618D6F987D2E4A464F4DB5D375CD63B9282276595BEFC876E7";
        }
        if (username == null) {
            username = "antonincherre.pro822@agentforce.com";
        }
        if (password == null) {
            password = "4yNFQiJbyQhTGrn";
        }
        if (securityToken == null) {
            securityToken = "jW6W80C7hOCYrfcpXjhmWmtY";
        }
        

        String fullPassword = password + securityToken;

        this.authenticator = new SalesforceAuthenticator(
            clientId, 
            clientSecret, 
            username, 
            fullPassword,
            loginUrl != null ? loginUrl : "https://login.salesforce.com"
        );
        this.connector = new SalesforceConnector(authenticator);
    }

    public List<VirtualLeadDTO> findLeads(double minRevenue, double maxRevenue, String province) {
        List<VirtualLeadDTO> leads = new ArrayList<>();
        try {

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT FirstName, LastName, Company, AnnualRevenue, Phone, Street, PostalCode, City, State, Country, CreatedDate ");
            queryBuilder.append("FROM Lead ");

            if (province != null && !province.trim().isEmpty()) {
                queryBuilder.append("WHERE State = '").append(province.replace("'", "\\'")).append("'");
            }
            
            String query = queryBuilder.toString();

            String jsonResponse = connector.executeQuery(query);
            JsonObject responseObj = JsonParser.parseString(jsonResponse).getAsJsonObject();

            if (responseObj.has("records")) {
                JsonArray records = responseObj.getAsJsonArray("records");
                for (JsonElement recordElem : records) {
                    JsonObject record = recordElem.getAsJsonObject();

                    if (record.has("AnnualRevenue") && !record.get("AnnualRevenue").isJsonNull()) {
                        double revenue = record.get("AnnualRevenue").getAsDouble();
                        if (revenue < minRevenue || revenue > maxRevenue) {
                            continue; // Exclure ce lead
                        }
                    } else {
                        if (minRevenue > 0) {
                            continue;
                        }
                    }
                    

                    if (province != null && !province.trim().isEmpty()) {
                        if (!record.has("State") || record.get("State").isJsonNull()) {
                            continue;
                        }
                        String recordState = record.get("State").getAsString();
                        if (!province.trim().equalsIgnoreCase(recordState.trim())) {
                            continue;
                        }
                    }

                    SalesforceLeadDTO salesforceLead = new SalesforceLeadDTO();
                    
                    if (record.has("FirstName") && !record.get("FirstName").isJsonNull())
                        salesforceLead.setFirstName(record.get("FirstName").getAsString());
                    if (record.has("LastName") && !record.get("LastName").isJsonNull())
                        salesforceLead.setLastName(record.get("LastName").getAsString());
                    if (record.has("Company") && !record.get("Company").isJsonNull())
                        salesforceLead.setCompany(record.get("Company").getAsString());
                    if (record.has("AnnualRevenue") && !record.get("AnnualRevenue").isJsonNull())
                        salesforceLead.setAnnualRevenue(record.get("AnnualRevenue").getAsDouble());
                    if (record.has("Phone") && !record.get("Phone").isJsonNull())
                        salesforceLead.setPhone(record.get("Phone").getAsString());
                    if (record.has("Street") && !record.get("Street").isJsonNull())
                        salesforceLead.setStreet(record.get("Street").getAsString());
                    if (record.has("PostalCode") && !record.get("PostalCode").isJsonNull())
                        salesforceLead.setPostalCode(record.get("PostalCode").getAsString());
                    if (record.has("City") && !record.get("City").isJsonNull())
                        salesforceLead.setCity(record.get("City").getAsString());
                    if (record.has("State") && !record.get("State").isJsonNull())
                        salesforceLead.setState(record.get("State").getAsString());
                    if (record.has("Country") && !record.get("Country").isJsonNull())
                        salesforceLead.setCountry(record.get("Country").getAsString());
                    if (record.has("CreatedDate") && !record.get("CreatedDate").isJsonNull())
                        salesforceLead.setCreatedDate(record.get("CreatedDate").getAsString());

                    VirtualLeadDTO lead = LeadMapper.toVirtualLead(salesforceLead);
                    leads.add(lead);
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leads;
    }

    public List<VirtualLeadDTO> findLeadsByDate(String fromIso, String toIso) {
        List<VirtualLeadDTO> leads = new ArrayList<>();
        try {
            String query = String.format(
                    "SELECT FirstName, LastName, Company, AnnualRevenue, Phone, Street, PostalCode, City, State, Country, CreatedDate "
                            +
                            "FROM Lead " +
                            "WHERE CreatedDate >= %s AND CreatedDate <= %s",
                    fromIso, toIso);
            // Note: SOQL date format might need adjustment (YYYY-MM-DDThh:mm:ssZ)

            String jsonResponse = connector.executeQuery(query);

            JsonObject responseObj = JsonParser.parseString(jsonResponse).getAsJsonObject();
            if (responseObj.has("records")) {
                JsonArray records = responseObj.getAsJsonArray("records");
                for (JsonElement recordElem : records) {
                    JsonObject record = recordElem.getAsJsonObject();
                    
                    // Créer un SalesforceLeadDTO
                    SalesforceLeadDTO salesforceLead = new SalesforceLeadDTO();
                    
                    if (record.has("FirstName") && !record.get("FirstName").isJsonNull())
                        salesforceLead.setFirstName(record.get("FirstName").getAsString());
                    if (record.has("LastName") && !record.get("LastName").isJsonNull())
                        salesforceLead.setLastName(record.get("LastName").getAsString());
                    if (record.has("Company") && !record.get("Company").isJsonNull())
                        salesforceLead.setCompany(record.get("Company").getAsString());
                    if (record.has("AnnualRevenue") && !record.get("AnnualRevenue").isJsonNull())
                        salesforceLead.setAnnualRevenue(record.get("AnnualRevenue").getAsDouble());
                    if (record.has("Phone") && !record.get("Phone").isJsonNull())
                        salesforceLead.setPhone(record.get("Phone").getAsString());
                    if (record.has("Street") && !record.get("Street").isJsonNull())
                        salesforceLead.setStreet(record.get("Street").getAsString());
                    if (record.has("PostalCode") && !record.get("PostalCode").isJsonNull())
                        salesforceLead.setPostalCode(record.get("PostalCode").getAsString());
                    if (record.has("City") && !record.get("City").isJsonNull())
                        salesforceLead.setCity(record.get("City").getAsString());
                    if (record.has("State") && !record.get("State").isJsonNull())
                        salesforceLead.setState(record.get("State").getAsString());
                    if (record.has("Country") && !record.get("Country").isJsonNull())
                        salesforceLead.setCountry(record.get("Country").getAsString());
                    if (record.has("CreatedDate") && !record.get("CreatedDate").isJsonNull())
                        salesforceLead.setCreatedDate(record.get("CreatedDate").getAsString());
                    
                    VirtualLeadDTO lead = LeadMapper.toVirtualLead(salesforceLead);
                    leads.add(lead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leads;
    }
}
