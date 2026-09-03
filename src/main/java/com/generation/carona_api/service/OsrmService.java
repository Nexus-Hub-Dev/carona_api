package com.generation.carona_api.service;

import com.fasterxml.jackson.databind.JsonNode; 
import com.generation.carona_api.dto.RouteResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Locale;

@Service
public class OsrmService {

    private final WebClient osrmWebClient;

    public OsrmService(@Qualifier("osrmWebClient") WebClient osrmWebClient) {
        this.osrmWebClient = osrmWebClient;
    }

    public RouteResult calcularRota(double latPartida, double lonPartida, double latDestino, double lonDestino) {
        String path = String.format(
                Locale.US,
                "/route/v1/driving/%f,%f;%f,%f?overview=false",
                lonPartida, latPartida, lonDestino, latDestino
        );

        try {
            JsonNode response = osrmWebClient.get()
                    .uri(path)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null || !response.has("routes") || response.get("routes").isEmpty()) {
                throw new IllegalArgumentException("Não foi possível calcular a rota entre os pontos informados.");
            }

            JsonNode route = response.get("routes").get(0);
            double distanciaKm = route.get("distance").asDouble() / 1000.0;
            double tempoMin = route.get("duration").asDouble() / 60.0;

            return new RouteResult(distanciaKm, tempoMin);

        } catch (Exception e) {
            throw new RuntimeException("Falha ao consultar serviço de rotas OSRM: " + e.getMessage(), e);
        }
    }
}