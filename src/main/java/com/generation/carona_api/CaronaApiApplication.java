package com.generation.carona_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.byteowls.jopencage.JOpenCageGeocoder;

@SpringBootApplication
public class CaronaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaronaApiApplication.class, args);
	}
	
	//Ensina o java a criar um objeto da API de coordenadas
	@Bean
    public JOpenCageGeocoder openCageGeocoder() {
        return new JOpenCageGeocoder("f59c5d31572f4e808604675cdab5cdff");
    }

}
