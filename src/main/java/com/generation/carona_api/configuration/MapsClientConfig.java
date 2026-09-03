package com.generation.carona_api.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MapsClientConfig {

    @Bean(name = "osrmWebClient")
    public WebClient osrmWebClient(
            @Value("${osrm.base-url:http://router.project-osrm.org}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "CaronaApi-Production/1.0 (contato@caronaapi.com)")
                .build();
    }

    @Bean(name = "nominatimWebClient")
    public WebClient nominatimWebClient(
            @Value("${nominatim.base-url:https://nominatim.openstreetmap.org}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "CaronaApi-Production/1.0 (grupo1.java85@gmail.com)")
                .defaultHeader("Referer", "https://carona-api-3ugi.onrender.com")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}