package com.generation.carona_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.generation.carona_api.dto.RouteResult;
import com.generation.carona_api.dto.SugestaoValorRequest;
import com.generation.carona_api.dto.SugestaoValorResponse;
import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.Veiculo;
import com.generation.carona_api.model.Viagem;
import com.generation.carona_api.repository.ViagemRepository;

@Service
public class ViagemService {

    private static final double VELOCIDADE_MEDIA_KMH = 60.0;
    private static final int RAIO_TERRA_KM = 6371;

    private static final double TARIFA_POR_KM = 2.50;

    private final ViagemRepository viagemRepository;
    private final ViagemMapsService viagemMapsService;

    public ViagemService(ViagemRepository viagemRepository, ViagemMapsService viagemMapsService) {
        this.viagemRepository = viagemRepository;
        this.viagemMapsService = viagemMapsService;
    }

    public Viagem calcularEntrega(Viagem viagem) {
        if (viagem.getLatitudePartida() == null || viagem.getLongitudePartida() == null ||
            viagem.getLatitudeDestino() == null || viagem.getLongitudeDestino() == null) {
            throw new IllegalArgumentException("As coordenadas de partida e destino não podem ser nulas.");
        }

        double distancia = calcularHaversine(
                viagem.getLatitudePartida(), viagem.getLongitudePartida(),
                viagem.getLatitudeDestino(), viagem.getLongitudeDestino());

        double tempo = (distancia / VELOCIDADE_MEDIA_KMH) * 60;
        double valorDaViagem = distancia * TARIFA_POR_KM;

        double distanciaArredondada = Math.round(distancia * 100.0) / 100.0;
        double tempoArredondado = Math.round(tempo * 100.0) / 100.0;
        double valorArredondado = Math.round(valorDaViagem * 100.0) / 100.0;

        viagem.setDistanciaKm(distanciaArredondada);
        viagem.setTempoEstimadoMin(tempoArredondado);
        viagem.setValorTotal(valorArredondado);

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

    // --- Regra "somente mulheres" ---

    public void validarCriacaoSomenteMulheres(Viagem viagem, Usuario motorista) {
        if (viagem.isSomenteMulheres() && !"F".equalsIgnoreCase(motorista.getSexo())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Apenas motoristas do sexo feminino podem criar viagens somente para mulheres"
            );
        }
    }

    public void validarReservaSomenteMulheres(Viagem viagem, Usuario usuario) {
        if (viagem.isSomenteMulheres() && !"F".equalsIgnoreCase(usuario.getSexo())) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Esta viagem é exclusiva para passageiras do sexo feminino"
            );
        }
    }

    public List<Viagem> listarSomenteMulheres() {
        return viagemRepository.findBySomenteMulheresTrue();
    }

    // --- Regra "PCD" (agora vinculada ao veículo, apenas filtro) ---

    public void validarCriacaoPCD(Viagem viagem, Veiculo veiculo) {
        if (viagem.isDisponivelPCD() && !veiculo.isAdaptadoPCD()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Só é possível marcar a viagem como disponível para PCD se o veículo for adaptado"
            );
        }
    }

    public List<Viagem> listarDisponivelPCD() {
        return viagemRepository.findByDisponivelPCDTrue();
    }

    // --- Sugestão de valor ---

    public SugestaoValorResponse calcularSugestaoValor(SugestaoValorRequest request) {
        RouteResult rota = viagemMapsService.calcularRota(request.getPartida(), request.getDestino());

        double valorSugerido = rota.distanciaKm() * TARIFA_POR_KM;
        double valorArredondado = Math.round(valorSugerido * 100.0) / 100.0;

        return new SugestaoValorResponse(rota.distanciaKm(), rota.tempoEstimadoMin(), valorArredondado);
    }
}