package com.generation.carona_api.dto;

import java.time.LocalDate;

import com.generation.carona_api.model.Usuario;

// Forma de resposta padrão para Usuario em qualquer endpoint (cadastro,
// GET por id, atualização) — nunca carrega senha.
public record UsuarioPublicoDTO(
		Long id,
		String nome,
		String nomeReal,
		String nomeSocial,
		String comoChamar,
		String usuario,
		String celular,
		String foto,
		String genero,
		LocalDate dataNascimento,
		Integer idade) {

	public static UsuarioPublicoDTO de(Usuario usuario) {
		if (usuario == null) return null;
		return new UsuarioPublicoDTO(
				usuario.getId(),
				usuario.getNome(),
				usuario.getNomeReal(),
				usuario.getNomeSocial(),
				usuario.getComoChamar(),
				usuario.getUsuario(),
				usuario.getCelular(),
				usuario.getFoto(),
				usuario.getGenero(),
				usuario.getDataNascimento(),
				usuario.getIdade());
	}
}
