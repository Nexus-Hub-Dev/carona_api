package com.generation.carona_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.generation.carona_api.dto.AddressResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class NominatimService {

    private final WebClient nominatimWebClient;

    public NominatimService(@Qualifier("nominatimWebClient") WebClient nominatimWebClient) {
        this.nominatimWebClient = nominatimWebClient;
    }

    public AddressResult geocodificar(String endereco) {
        try {
            // Pausa de 1.5s para garantir que partida e destino não colidam no rate-limit
            Thread.sleep(1500);

            JsonNode response = nominatimWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", endereco)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    // Se o Nominatim responder 429, aguarda 2s e tenta novamente (até 3x) de forma transparente
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests))
                    .block();

            if (response == null || !response.isArray() || response.isEmpty()) {
                throw new IllegalArgumentException("Endereço não encontrado: " + endereco);
            }

            JsonNode item = response.get(0);
            return new AddressResult(
                    item.get("display_name").asText(),
                    item.get("lat").asDouble(),
                    item.get("lon").asDouble()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro de interrupção na consulta de endereço", e);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao consultar serviço de mapas: " + e.getMessage(), e);
        }
    }
}