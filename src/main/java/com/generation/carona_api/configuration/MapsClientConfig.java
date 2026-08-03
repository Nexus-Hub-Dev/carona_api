package com.generation.carona_api.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MapsClientConfig {

    @Bean
    public WebClient osrmWebClient(@Value("${osrm.base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "carona-api-dev")
                .build();
    }

    @Bean
    public WebClient nominatimWebClient(@Value("${nominatim.base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "carona-api-dev")
                .build();
    }
}