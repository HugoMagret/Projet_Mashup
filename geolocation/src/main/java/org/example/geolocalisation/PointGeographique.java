package org.example.geolocalisation;

/**
 * Représente un résultat de géolocalisation minimal (latitude, longitude + nom affiché).
 */
public record PointGeographique(double latitude, double longitude, String nomAffiche) {
    public Double getLatitude() {
        return  latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getNomAffiche() {
        return nomAffiche;
    }
}
