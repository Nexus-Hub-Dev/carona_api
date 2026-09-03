package com.generation.carona_api.service;

import com.generation.carona_api.dto.AddressResult;
import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NominatimService {

    private final WebClient nominatimWebClient;
    private final String contactEmail;

    public NominatimService(@Qualifier("nominatimWebClient") WebClient nominatimWebClient,
                            @Value("${nominatim.contact-email:grupo1.java85@gmail.com}") String contactEmail) {
        this.nominatimWebClient = nominatimWebClient;
        this.contactEmail = contactEmail;
    }

    public AddressResult geocodificar(String endereco) {
        JsonNode response = nominatimWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", endereco)
                        .queryParam("format", "jsonv2")
                        .queryParam("addressdetails", 1)
                        .queryParam("limit", 1)
                        .queryParam("email", contactEmail)
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