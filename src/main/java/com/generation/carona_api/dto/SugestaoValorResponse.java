package com.generation.carona_api.dto;

public class SugestaoValorResponse {

    private Double distanciaKm;
    private Double tempoEstimadoMin;
    private Double valorSugerido;

    public SugestaoValorResponse(Double distanciaKm, Double tempoEstimadoMin, Double valorSugerido) {
        this.distanciaKm = distanciaKm;
        this.tempoEstimadoMin = tempoEstimadoMin;
        this.valorSugerido = valorSugerido;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public Double getTempoEstimadoMin() {
        return tempoEstimadoMin;
    }

    public Double getValorSugerido() {
        return valorSugerido;
    }
}