package org.example.geolocalisation;

/**
 * Représente un résultat de géolocalisation minimal (latitude, longitude + nom affiché).
 */
public record PointGeographique(double latitude, double longitude, String nomAffiche) {}
