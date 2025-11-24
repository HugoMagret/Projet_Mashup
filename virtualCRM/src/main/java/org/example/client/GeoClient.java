package org.example.client;

import org.example.geolocalisation.ServiceGeolocalisation;

import org.example.dto.GeographicPointDTO;
import org.springframework.stereotype.Component;

@Component
public class GeoClient {

    private final ServiceGeolocalisation client = new ServiceGeolocalisation();

    public GeographicPointDTO geocode(String address) {
        return client.geolocaliserAdresse(address)
                .map(p -> {
                    GeographicPointDTO dto = new GeographicPointDTO();
                    dto.setLatitude(p.getLatitude());
                    dto.setLongitude(p.getLongitude());
                    return dto;
                })
                .orElse(null);
    }
}