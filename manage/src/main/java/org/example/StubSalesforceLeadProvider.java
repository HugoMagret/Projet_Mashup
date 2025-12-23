package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.internal.InternalLeadDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation concrète qui interroge Salesforce via SalesforceConnector et
 * convertit les résultats en InternalLeadDTO prêts à être insérés dans InternalCRM.
 */
public class StubSalesforceLeadProvider implements SalesforceLeadProvider {

    private static final String DEFAULT_CLIENT_ID = "3MVG9dAEux2v1sLtbdQ7kVGBVRvnIT0JhqShfbp5Oao2IeKy71uEOygjkHYmwzP81u.M5ry_Ihi_x_DX3uOQJ";
    private static final String DEFAULT_CLIENT_SECRET = "4CA14F77B4FCAF618D6F987D2E4A464F4DB5D375CD63B9282276595BEFC876E7";
    private static final String DEFAULT_USERNAME = "antonincherre.pro822@agentforce.com";
    private static final String DEFAULT_PASSWORD = "4yNFQiJbyQhTGrn";
    private static final String DEFAULT_SECURITY_TOKEN = "jW6W80C7hOCYrfcpXjhmWmtY";
    private static final String DEFAULT_LOGIN_URL = "https://login.salesforce.com";

    private static final String SOQL_QUERY = String.join(" ",
            "SELECT Id, FirstName, LastName, Company, AnnualRevenue, Phone, Street, PostalCode, City, State, Country, CreatedDate,",
            "Email, LeadSource, Status, ConvertedAccountId, Industry, Description",
            "FROM Lead ORDER BY LastName");

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<InternalLeadDTO> fetchAllLeadsAsInternalDto() throws Exception {
        SalesforceConnector connector = new SalesforceConnector(createAuthenticator());
        String jsonResponse = connector.executeQuery(SOQL_QUERY);
        return parseLeads(jsonResponse);
    }

    private SalesforceAuthenticator createAuthenticator() {
        String fullPassword = DEFAULT_PASSWORD + DEFAULT_SECURITY_TOKEN;
        return new SalesforceAuthenticator(
                DEFAULT_CLIENT_ID,
                DEFAULT_CLIENT_SECRET,
                DEFAULT_USERNAME,
                fullPassword,
                DEFAULT_LOGIN_URL
        );
    }

    private List<InternalLeadDTO> parseLeads(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode records = root.path("records");

        List<InternalLeadDTO> leads = new ArrayList<>();
        if (!records.isArray()) {
            return leads;
        }

        for (JsonNode record : records) {
            InternalLeadDTO dto = new InternalLeadDTO();
            dto.setFirstName(textValue(record, "FirstName"));
            dto.setLastName(textValue(record, "LastName"));
            dto.setCompanyName(textValue(record, "Company"));
            dto.setAnnualRevenue(numberValue(record, "AnnualRevenue"));
            dto.setPhone(textValue(record, "Phone"));
            dto.setStreet(textValue(record, "Street"));
            dto.setPostalCode(textValue(record, "PostalCode"));
            dto.setCity(textValue(record, "City"));
            dto.setState(textValue(record, "State"));
            dto.setCountry(textValue(record, "Country"));
            dto.setCreationDate(textValue(record, "CreatedDate"));

            leads.add(dto);
        }

        return leads;
    }

    private String textValue(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return "";
        }
        String value = fieldNode.asText("");
        return value == null ? "" : value;
    }

    private double numberValue(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull()) {
            return 0.0;
        }
        if (fieldNode.isNumber()) {
            return fieldNode.asDouble();
        }
        String text = fieldNode.asText("").replaceAll("[^0-9.,-]", "");
        if (text.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

}

