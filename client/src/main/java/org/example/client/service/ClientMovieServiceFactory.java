package org.example.client.service;


import java.lang.reflect.InvocationTargetException;

public class ClientMovieServiceFactory {

    private final static String SERVICE_CLASSNAME
            = "org.example.client.service.rest.RestClientMovieService";
    private static Class<ClientMovieService> serviceClass = null;

    private ClientMovieServiceFactory() {
    }

    @SuppressWarnings("unchecked")
    private synchronized static Class<ClientMovieService> getServiceClass() {

        if (serviceClass == null) {
            try {
                serviceClass = (Class<ClientMovieService>) Class.forName(SERVICE_CLASSNAME);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return serviceClass;

    }

    public static ClientMovieService getService() {

        try {
            return (ClientMovieService) getServiceClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
