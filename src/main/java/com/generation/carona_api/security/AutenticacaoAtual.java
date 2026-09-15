package com.generation.carona_api.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.repository.UsuarioRepository;

// O filtro JWT só guarda usuário/senha (UserDetailsImpl) no contexto de
// segurança — quando um controller precisa do Usuario de verdade (id,
// gênero, etc., para checar dono/regra de negócio), busca aqui.
@Component
public class AutenticacaoAtual {

	private final UsuarioRepository usuarioRepository;

	public AutenticacaoAtual(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public Usuario obter() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return usuarioRepository.findByUsuario(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado."));
	}
}
