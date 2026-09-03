package com.generation.carona_api.service;

import com.generation.carona_api.dto.AddressResult;
import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class NominatimService {

    private final WebClient nominatimWebClient;

    public NominatimService(@Qualifier("nominatimWebClient") WebClient nominatimWebClient) {
        this.nominatimWebClient = nominatimWebClient;
    }

    public AddressResult geocodificar(String endereco) {
        try {
            // Pausa de segurança de 1 segundo para respeitar a política de uso do Nominatim (max 1 req/sec)
            Thread.sleep(1000);

            JsonNode response = nominatimWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", endereco)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
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

        } catch (WebClientResponseException.TooManyRequests e) {
            // Trata o erro 429 para não quebrar a API inteira
            throw new RuntimeException("O serviço de mapas externo está sobrecarregado (429). Tente novamente em alguns segundos.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro de interrupção na consulta de endereço", e);
        }
    }
}