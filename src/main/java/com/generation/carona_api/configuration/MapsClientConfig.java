package com.generation.carona_api.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
//AQUI ESTÃO OS IMPORTS DA "VACINA" QUE EVITAM O ERRO VERMELHO
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;



@Configuration
public class MapsClientConfig {
/*
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
    */
	@Bean
    public WebClient osrmWebClient(@Value("${osrm.base-url}") String baseUrl) {
        // Vacina aplicada: Força o Java a usar o DNS normal da máquina
        HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "carona-api-dev")
                .build();
    }

    @Bean
    public WebClient nominatimWebClient(@Value("${nominatim.base-url}") String baseUrl) {
        // Vacina aplicada para o Nominatim também!
        HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader("User-Agent", "carona-api-dev")
                .build();
    
}
    }