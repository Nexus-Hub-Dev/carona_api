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