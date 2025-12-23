package org.example.geolocalisation;

/** Exception fonctionnelle simple pour le service de géolocalisation. */
public class GeolocalisationException extends RuntimeException {
    public GeolocalisationException(String message) { super(message); }
    public GeolocalisationException(String message, Throwable cause) { super(message, cause); }
}
