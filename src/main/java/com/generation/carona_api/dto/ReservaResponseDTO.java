package com.generation.carona_api.dto;

import java.time.LocalDateTime;

import com.generation.carona_api.model.Reserva;
import com.generation.carona_api.model.StatusReserva;

public record ReservaResponseDTO(
		Long id,
		Long viagemId,
		Long passageiroId,
		StatusReserva status,
		String motivo,
		LocalDateTime criadoEm,
		LocalDateTime atualizadoEm,
		ViagemResponseDTO viagem,
		UsuarioResumoDTO passageiro) {

	public static ReservaResponseDTO de(Reserva reserva, int vagasRestantesDaViagem) {
		return new ReservaResponseDTO(
				reserva.getId(),
				reserva.getViagem().getId(),
				reserva.getPassageiro().getId(),
				reserva.getStatus(),
				reserva.getMotivo(),
				reserva.getCriadoEm(),
				reserva.getAtualizadoEm(),
				ViagemResponseDTO.de(reserva.getViagem(), vagasRestantesDaViagem),
				UsuarioResumoDTO.de(reserva.getPassageiro()));
	}
}
