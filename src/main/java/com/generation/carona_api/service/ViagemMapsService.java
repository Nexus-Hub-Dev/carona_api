package com.generation.carona_api.service;

import com.generation.carona_api.dto.AddressResult;
import com.generation.carona_api.dto.RouteResult;
import com.generation.carona_api.model.Viagem;
import org.springframework.stereotype.Service;

@Service
public class ViagemMapsService {

    private final NominatimService nominatimService;
    private final OsrmService osrmService;

    public ViagemMapsService(NominatimService nominatimService, OsrmService osrmService) {
        this.nominatimService = nominatimService;
        this.osrmService = osrmService;
    }

    public void preencherDadosRota(Viagem viagem) {
        AddressResult origem = nominatimService.geocodificar(viagem.getPartida());
        AddressResult destino = nominatimService.geocodificar(viagem.getDestino());

        viagem.setLatitudePartida(origem.latitude());
        viagem.setLongitudePartida(origem.longitude());
        viagem.setLatitudeDestino(destino.latitude());
        viagem.setLongitudeDestino(destino.longitude());

        RouteResult rota = osrmService.calcularRota(
                origem.latitude(), origem.longitude(),
                destino.latitude(), destino.longitude()
        );

        viagem.setDistanciaKm(rota.distanciaKm());
        viagem.setTempoEstimadoMin(rota.tempoEstimadoMin());
    }
}