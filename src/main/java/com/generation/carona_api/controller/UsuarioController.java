package com.generation.carona_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.carona_api.dto.AtualizarUsuarioRequest;
import com.generation.carona_api.dto.UsuarioPublicoDTO;
import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.UsuarioLogin;
import com.generation.carona_api.security.AutenticacaoAtual;
import com.generation.carona_api.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private AutenticacaoAtual autenticacaoAtual;

	// Autenticado (ver SecurityConfig: só /logar e /cadastrar são
	// públicos) — quem está logado pode ver o perfil de qualquer usuário
	// pelo id, igual ao mock.
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioPublicoDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(usuarioService.getById(id));
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<UsuarioPublicoDTO> cadastrar(@RequestBody Usuario usuario) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(usuario));
	}

	@PostMapping("/logar")
	public ResponseEntity<UsuarioLogin> autenticar(@RequestBody UsuarioLogin usuarioLogin) {
		return ResponseEntity.ok(usuarioService.autenticar(usuarioLogin.getUsuario(), usuarioLogin.getSenha()));
	}

	@PutMapping("/atualizar")
	public ResponseEntity<UsuarioPublicoDTO> atualizar(@Valid @RequestBody AtualizarUsuarioRequest dados) {
		return ResponseEntity.ok(usuarioService.atualizar(dados, autenticacaoAtual.obter()));
	}
}
