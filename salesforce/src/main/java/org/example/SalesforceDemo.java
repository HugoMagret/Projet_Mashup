package org.example;

public class SalesforceDemo {
    public static void main(String[] args) {
        String accessToken = "00DgL00000F4QGD!AQEAQBAO_GAJSwhrc8Mt.xx9FZ_OR4ukaNkVtIvEwHrXz2mXiDPbSoL3diDhlFNknk1zlLrKZHX2yUz4RXtQHQ7xGKZamWxv";
        String instanceUrl = "https://orgfarm-fccab8c3ff-dev-ed.develop.my.salesforce.com";

        SalesforceConnector connector = new SalesforceConnector(accessToken, instanceUrl);

        try {
            String query = "SELECT Id, Name FROM Account LIMIT 5";
            System.out.println("Executing query: " + query);
            String result = connector.executeQuery(query);
            System.out.println("Query Result:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Error executing query:");
            e.printStackTrace();
        }
    }
}
