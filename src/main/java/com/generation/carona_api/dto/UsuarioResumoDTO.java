package com.generation.carona_api.dto;

import com.generation.carona_api.model.Usuario;

// Recorte público de Usuario usado dentro de outras respostas (viagem,
// reserva, mensagem) — nunca inclui senha nem os campos de identidade
// crus (nomeReal/nomeSocial/comoChamar), só o nome já calculado.
public record UsuarioResumoDTO(Long id, String nome, String foto, String genero) {

	public static UsuarioResumoDTO de(Usuario usuario) {
		if (usuario == null) return null;
		return new UsuarioResumoDTO(usuario.getId(), usuario.getNome(), usuario.getFoto(), usuario.getGenero());
	}
}
