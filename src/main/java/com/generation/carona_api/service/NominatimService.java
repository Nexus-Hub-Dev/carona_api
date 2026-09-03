package com.generation.carona_api.service;

import com.generation.carona_api.dto.AddressResult;
import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NominatimService {

    private static final long MIN_INTERVAL_MS = 1100L;

    private final WebClient nominatimWebClient;
    private final String contactEmail;
    private final Map<String, AddressResult> cache = new ConcurrentHashMap<>();
    private final Object rateLimitLock = new Object();
    private volatile long lastRequestAt = 0L;

    public NominatimService(@Qualifier("nominatimWebClient") WebClient nominatimWebClient,
                            @Value("${nominatim.contact-email:grupo1.java85@gmail.com}") String contactEmail) {
        this.nominatimWebClient = nominatimWebClient;
        this.contactEmail = contactEmail;
    }

    public AddressResult geocodificar(String endereco) {
        String chaveCache = normalizar(endereco);
        AddressResult cached = cache.get(chaveCache);
        if (cached != null) {
            return cached;
        }

        aguardarRateLimit();

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
}