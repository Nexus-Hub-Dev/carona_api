package com.generation.carona_api.service;

import com.generation.carona_api.dto.AddressResult;
import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NominatimService {

    private static final long MIN_INTERVAL_MS = 1100L;
    private static final int MAX_TENTATIVAS = 3;

    private final WebClient nominatimWebClient;
    private final WebClient photonWebClient;
    private final WebClient fallbackWebClient;
    private final String contactEmail;
    private final Map<String, AddressResult> cache = new ConcurrentHashMap<>();
    private final Object rateLimitLock = new Object();
    private volatile long lastRequestAt = 0L;

    public NominatimService(@Qualifier("nominatimWebClient") WebClient nominatimWebClient,
                            @Value("${nominatim.contact-email:grupo1.java85@gmail.com}") String contactEmail) {
        this.nominatimWebClient = nominatimWebClient;
        this.photonWebClient = WebClient.builder()
                .baseUrl("https://photon.komoot.io")
                .defaultHeader("Accept", "application/json")
                .build();
        this.fallbackWebClient = WebClient.builder()
                .baseUrl("https://geocode.maps.co")
                .defaultHeader("Accept", "application/json")
                .build();
        this.contactEmail = contactEmail;
    }

    public AddressResult geocodificar(String endereco) {
        String chaveCache = normalizar(endereco);
        AddressResult cached = cache.get(chaveCache);
        if (cached != null) {
            return cached;
        }

        aguardarRateLimit();

        JsonNode response = consultarNominatimComRetry(endereco);

        if (response == null || !response.isArray() || response.isEmpty()) {
            response = consultarPhoton(endereco);
        }

        if (response == null || !response.isArray() || response.isEmpty()) {
            response = consultarFallbackMapsCo(endereco);
        }

        if (response == null || !response.isArray() || response.isEmpty()) {
            throw new IllegalArgumentException("Endereço não encontrado: " + endereco);
        }

        JsonNode item = response.get(0);
        AddressResult result = new AddressResult(
                item.get("display_name").asText(),
                item.get("lat").asDouble(),
                item.get("lon").asDouble()
        );

        cache.put(chaveCache, result);
        return result;
    }

    private String normalizar(String endereco) {
        return endereco == null ? "" : endereco.trim().toLowerCase(Locale.ROOT);
    }

    private void aguardarRateLimit() {
        synchronized (rateLimitLock) {
            long agora = System.currentTimeMillis();
            long proximaLiberacao = lastRequestAt + MIN_INTERVAL_MS;
            long espera = proximaLiberacao - agora;

            if (espera > 0) {
                try {
                    Thread.sleep(espera);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrompido ao aguardar liberação para consulta ao Nominatim.", e);
                }
            }

            lastRequestAt = System.currentTimeMillis();
        }
    }

    private JsonNode consultarNominatimComRetry(String endereco) {
        RuntimeException ultimoErro = null;

        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            try {
                return nominatimWebClient.get()
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
            } catch (WebClientResponseException e) {
                ultimoErro = new RuntimeException("Nominatim retornou " + e.getStatusCode().value(), e);
            } catch (RuntimeException e) {
                ultimoErro = e;
            }

            if (tentativa < MAX_TENTATIVAS) {
                aguardarRateLimit();
            }
        }

        if (ultimoErro != null) {
            throw ultimoErro;
        }

        return null;
    }

    private JsonNode consultarFallback(String endereco) {
        try {
            return fallbackWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", endereco)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private JsonNode consultarPhoton(String endereco) {
        try {
            JsonNode response = photonWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api")
                            .queryParam("q", endereco)
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response == null || !response.has("features") || !response.get("features").isArray()
                    || response.get("features").isEmpty()) {
                return null;
            }

            JsonNode feature = response.get("features").get(0);
            JsonNode coordinates = feature.path("geometry").path("coordinates");
            if (!coordinates.isArray() || coordinates.size() < 2) {
                return null;
            }

            JsonNode converted = feature.deepCopy();
            ((tools.jackson.databind.node.ObjectNode) converted).put("display_name",
                    feature.path("properties").path("name").asText("Endereço encontrado"));
            ((tools.jackson.databind.node.ObjectNode) converted).put("lat", coordinates.get(1).asDouble());
            ((tools.jackson.databind.node.ObjectNode) converted).put("lon", coordinates.get(0).asDouble());

            tools.jackson.databind.node.ArrayNode normalized =
                    tools.jackson.databind.node.JsonNodeFactory.instance.arrayNode();
            normalized.add(converted);
            return normalized;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private JsonNode consultarFallbackMapsCo(String endereco) {
        return consultarFallback(endereco);
    }
}