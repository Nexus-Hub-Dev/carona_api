package com.generation.carona_api.dto;

import java.time.LocalDateTime;

import com.generation.carona_api.model.Mensagem;

public record MensagemResponseDTO(
		Long id,
		Long reservaId,
		Long autorId,
		String texto,
		LocalDateTime criadoEm,
		UsuarioResumoDTO autor) {

	public static MensagemResponseDTO de(Mensagem mensagem) {
		return new MensagemResponseDTO(
				mensagem.getId(),
				mensagem.getReserva().getId(),
				mensagem.getAutor().getId(),
				mensagem.getTexto(),
				mensagem.getCriadoEm(),
				UsuarioResumoDTO.de(mensagem.getAutor()));
	}
}
