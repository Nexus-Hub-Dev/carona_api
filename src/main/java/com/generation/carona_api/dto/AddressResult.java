package com.generation.carona_api.dto;

public class AddressResult {
    private final String displayName;
    private final double latitude;
    private final double longitude;

    public AddressResult(String displayName, double latitude, double longitude) {
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String displayName() { return displayName; }
    public double latitude() { return latitude; }
    public double longitude() { return longitude; }

    // + equals(), hashCode() e toString() gerados automaticamente
}