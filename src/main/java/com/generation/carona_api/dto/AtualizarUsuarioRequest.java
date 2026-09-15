package com.generation.carona_api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

// Atualização parcial de perfil: só os campos enviados são alterados. A
// senha aqui NUNCA troca a senha salva — é só a senha ATUAL, usada para
// confirmar que quem está editando é o dono da conta (mesma regra do
// mock: se não bater, 401 "Senha atual incorreta.").
public record AtualizarUsuarioRequest(
		Long id,
		String nomeReal,
		String nomeSocial,
		String comoChamar,
		String usuario,
		String celular,
		String foto,
		String genero,
		LocalDate dataNascimento,
		@NotBlank(message = "Informe sua senha atual para confirmar a alteração.") String senha) {
}
