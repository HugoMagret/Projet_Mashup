package org.example;

import java.io.IOException;

/**
 * Classe principale pour récupérer les données depuis Salesforce
 * Basée sur la requête du fichier retrieve.sh
 */
public class SalesforceDataRetriever {
    
    // Token et instance URL directement
    private static final String ACCESS_TOKEN = "00DgL00000F4QGD!AQEAQIqOdiJmzP4Hlw5pI3PC3lI0r26050rf6LB496Qh.xAxTnUfZbrwlFBYtKZB9cgBTPB1YwHoy4hYVjk4khE9FIeLY.WB";
    private static final String INSTANCE_URL = "https://orgfarm-fccab8c3ff-dev-ed.develop.my.salesforce.com";

    public static void main(String[] args) {
        try {
            
            SalesforceConnector connector = new SalesforceConnector(ACCESS_TOKEN, INSTANCE_URL);
            
            
            String query = "SELECT FirstName, LastName, ConvertedAccountId FROM Lead";
            
            System.out.println("🔍 Récupération des données depuis Salesforce...");
            System.out.println("📋 Requête SOQL: " + query);
            System.out.println("🌐 Instance URL: " + INSTANCE_URL);
            System.out.println();
            
            // Exécuter la requête
            String result = connector.executeQuery(query);
            
            System.out.println("✅ Résultat de la requête:");
            System.out.println(result);
            
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la récupération des données:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("❌ Requête interrompue:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue:");
            e.printStackTrace();
        }
    }
}

