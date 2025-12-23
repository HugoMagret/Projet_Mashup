package org.example.config;

import org.example.client.ClientFactory;
import org.example.client.GeoClient;
import org.example.client.InternalCRMClient;
import org.example.client.SalesforceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    private final ClientFactory clientFactory;

    public ClientConfig(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Bean
    public InternalCRMClient internalCRMClient() throws Exception {
        return clientFactory.createInternalCRMClient("localhost", 9090);
    }

    @Bean
    public SalesforceClient salesforceClient() {
        return clientFactory.createSalesforceClient();
    }

    @Bean
    public GeoClient geoClient() {
        return clientFactory.createGeoClient();
    }

}
