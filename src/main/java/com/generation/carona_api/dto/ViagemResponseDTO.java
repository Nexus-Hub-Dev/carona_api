package com.generation.carona_api.dto;

import java.time.LocalDateTime;

import com.generation.carona_api.model.Viagem;

// Forma de resposta padrão para Viagem: usuario/veiculo aparecem só como
// recorte (nunca a entidade completa, pra não vazar senha nem criar
// referência circular), e vagasRestantes é calculado a partir das
// reservas aceitas — não existe como coluna.
public record ViagemResponseDTO(
		Long id,
		String partida,
		String destino,
		String bairroDestino,
		LocalDateTime data,
		boolean disponivelPCD,
		Boolean apenasMulheres,
		boolean aceitaPet,
		Double valorSugerido,
		Double valorTotal,
		Integer vagasDisponiveis,
		int vagasRestantes,
		Double distanciaKm,
		Double tempoEstimadoMin,
		Integer velocidadeMedia,
		Double latitudePartida,
		Double latitudeDestino,
		Double longitudePartida,
		Double longitudeDestino,
		UsuarioResumoDTO usuario,
		VeiculoResumoDTO veiculo) {

	public static ViagemResponseDTO de(Viagem viagem, int vagasRestantes) {
		return new ViagemResponseDTO(
				viagem.getId(),
				viagem.getPartida(),
				viagem.getDestino(),
				viagem.getBairroDestino(),
				viagem.getData(),
				viagem.isDisponivelPCD(),
				viagem.getApenasMulheres(),
				viagem.isAceitaPet(),
				viagem.getValorSugerido(),
				viagem.getValorTotal(),
				viagem.getVagasDisponiveis(),
				vagasRestantes,
				viagem.getDistanciaKm(),
				viagem.getTempoEstimadoMin(),
				viagem.getVelocidadeMedia(),
				viagem.getLatitudePartida(),
				viagem.getLatitudeDestino(),
				viagem.getLongitudePartida(),
				viagem.getLongitudeDestino(),
				UsuarioResumoDTO.de(viagem.getUsuario()),
				VeiculoResumoDTO.de(viagem.getVeiculo()));
	}
}
