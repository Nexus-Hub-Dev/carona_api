package com.generation.carona_api.service;

import com.generation.carona_api.dto.AddressResult;
import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class NominatimService {

    private final WebClient nominatimWebClient;

    public NominatimService(@Qualifier("nominatimWebClient") WebClient nominatimWebClient) {
        this.nominatimWebClient = nominatimWebClient;
    }

    public AddressResult geocodificar(String endereco) {
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
    }
}