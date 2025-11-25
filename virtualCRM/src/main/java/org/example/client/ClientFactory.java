package org.example.client;

import org.springframework.stereotype.Component;

@Component
public class ClientFactory {

    public InternalCRMClient createInternalCRMClient(String host, int port) throws Exception {
        return new InternalCRMClient(host, port);
    }

    public SalesforceClient createSalesforceClient() {
        return new SalesforceClient();
    }

    public GeoClient createGeoClient() {
        return new GeoClient();
    }
}
