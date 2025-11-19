package org.example.dto;

public class GeographicPointDTO {

    private Double latitude;
    private Double longitude;

    public Double getLongitude() {
        return longitude;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
