package com.generation.carona_api.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.generation.carona_api.model.Viagem;

@Service
public class ViagemService {

    private static final double VELOCIDADE_MEDIA_KMH = 60.0;
    private static final int RAIO_TERRA_KM = 6371;

    public Viagem calcularEntrega(Viagem viagem) {

        double distancia = calcularHaversine(
                viagem.getLatitudePartida(), viagem.getLongitudePartida(),
                viagem.getLatitudeDestino(), viagem.getLongitudeDestino());

        double tempo = distancia / VELOCIDADE_MEDIA_KMH * 60;

        viagem.setDistanciaKm(BigDecimal.valueOf(distancia));
        viagem.setTempoEstimadoMin(tempo);

        return viagem;
    }

    private double calcularHaversine(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_KM * c;
    }
}