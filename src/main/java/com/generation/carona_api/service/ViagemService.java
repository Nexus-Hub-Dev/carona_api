package com.generation.carona_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.generation.carona_api.model.Viagem;
import com.generation.carona_api.repository.ViagemRepository;

@Service
public class ViagemService {

	
	//Vai criar um objeto dessa classe q esta la na MAIN para acessar a API de geolocalização
		@Autowired
	    private JOpenCageGeocoder jOpenCageGeocoder;
		@Autowired
		private ViagemRepository viagemRepository;
		
    private static final double VELOCIDADE_MEDIA_KMH = 60.0;
    private static final int RAIO_TERRA_KM = 6371;
    
    private static final double TARIFA_POR_KM = 2.50; 
 // Método único para buscar qualquer coordenada no OpenCage
    public double[] obterCoordenadas(String endereco) {
        
        // 1. Prepara a requisição com o endereço em texto
        JOpenCageForwardRequest request = new JOpenCageForwardRequest(endereco);

        // 2. AQUI ACESSA O SITE! O método .forward() faz a mágica
        JOpenCageResponse response = jOpenCageGeocoder.forward(request);

        // 3. Pega a primeira coordenada que a API encontrou
        JOpenCageLatLng coordenadas = response.getFirstPosition();

        if (coordenadas != null) {
            // Retorna um vetor onde a posição 0 é a Latitude e a 1 é a Longitude
            return new double[]{ coordenadas.getLat(), coordenadas.getLng() };
        }
        
        // Retorna zero caso não encontre o endereço
        return new double[]{ 0.0, 0.0 }; 
    }
public Viagem calcularEntrega(Viagem viagem) {
        
        // 1. BUSCA AS COORDENADAS PRIMEIRO
        double[] coordsPartida = obterCoordenadas(viagem.getPartida());
        double[] coordsDestino = obterCoordenadas(viagem.getDestino());

        // 2. PREENCHE O OBJETO
        viagem.setLatitudePartida(coordsPartida[0]);
        viagem.setLongitudePartida(coordsPartida[1]);
        viagem.setLatitudeDestino(coordsDestino[0]);
        viagem.setLongitudeDestino(coordsDestino[1]);

        // 3. AGORA FAZ A CHECAGEM (que não vai mais dar erro!)
        if (viagem.getLatitudePartida() == 0.0 || viagem.getLongitudePartida() == 0.0 ||
            viagem.getLatitudeDestino() == 0.0 || viagem.getLongitudeDestino() == 0.0) {
            throw new IllegalArgumentException("Não foi possível encontrar as coordenadas para os endereços informados.");
        }

        // 4. FAZ A MATEMÁTICA
        double distancia = calcularHaversine(
                viagem.getLatitudePartida(), viagem.getLongitudePartida(),
                viagem.getLatitudeDestino(), viagem.getLongitudeDestino());

        // Tempo = (Distância / Velocidade) * 60 minutos
        double tempo = (distancia / VELOCIDADE_MEDIA_KMH) * 60;
        
        // Cálculo do valor da viagem
        double valorDaViagem = distancia * TARIFA_POR_KM;

        // Arredondando tudo para 2 casas decimais
        double distanciaArredondada = Math.round(distancia * 100.0) / 100.0;
        double tempoArredondado = Math.round(tempo * 100.0) / 100.0;
        double valorArredondado = Math.round(valorDaViagem * 100.0) / 100.0;

        // Setando os valores formatados no objeto Viagem
        viagem.setDistanciaKm(distanciaArredondada);
        viagem.setTempoEstimadoMin(tempoArredondado);
        viagem.setValorKm(valorArredondado);

        // Retorna o objeto completo para o Controller salvar no banco
        return viagem;
    }

    private double calcularHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_KM * c;
    }
    
    // Método único para buscar qualquer coordenada
   /* public double[] obterCoordenadas(String endereco) {
        
        // 1. Prepara a requisição com o endereço em texto
        JOpenCageForwardRequest request = new JOpenCageForwardRequest(endereco);

        // 2. AQUI ACESSA O SITE! O método .forward() faz a mágica
        JOpenCageResponse response = jOpenCageGeocoder.forward(request);

        // 3. Pega a primeira coordenada que a API encontrou
        JOpenCageLatLng coordenadas = response.getFirstPosition();

        if (coordenadas != null) {
            // Retorna um vetor onde a posição 0 é a Latitude e a 1 é a Longitude
            return new double[]{ coordenadas.getLat(), coordenadas.getLng() };
        }
        
        // Retorna zero caso não encontre o endereço
        return new double[]{ 0.0, 0.0 }; 
    }
    
 // AQUI ENTRA O CÓDIGO NOVO: O método que o Controller vai chamar
    public Viagem salvarViagem(Viagem viagem) {
        
        // 1. Pega as coordenadas usando o texto que veio do Insomnia
        double[] coordsPartida = obterCoordenadas(viagem.getPartida());
        double[] coordsDestino = obterCoordenadas(viagem.getDestino());

        // 2. Salva as coordenadas dentro do objeto Viagem
        viagem.setLatitudePartida(coordsPartida[0]);
        viagem.setLongitudePartida(coordsPartida[1]);
        
        viagem.setLatitudeDestino(coordsDestino[0]);
        viagem.setLongitudeDestino(coordsDestino[1]);

        // 3. Salva a viagem no Banco de Dados e retorna ela pronta
        return viagemRepository.save(viagem);
    }*/
}