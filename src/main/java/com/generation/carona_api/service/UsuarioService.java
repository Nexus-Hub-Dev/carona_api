package com.generation.carona_api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.carona_api.dto.AtualizarUsuarioRequest;
import com.generation.carona_api.dto.UsuarioPublicoDTO;
import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.UsuarioLogin;
import com.generation.carona_api.repository.UsuarioRepository;
import com.generation.carona_api.security.JwtService;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UsuarioPublicoDTO getById(Long id) {
		return usuarioRepository.findById(id)
				.map(UsuarioPublicoDTO::de)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
	}

	// Mesmos campos obrigatórios do mock: nomeReal, usuario, senha,
	// celular, gênero e data de nascimento.
	public UsuarioPublicoDTO cadastrar(Usuario usuario) {
		if (isBlank(usuario.getNomeReal()) || isBlank(usuario.getUsuario()) || isBlank(usuario.getSenha())
				|| isBlank(usuario.getCelular()) || isBlank(usuario.getGenero()) || usuario.getDataNascimento() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Nome real, e-mail, senha, celular, gênero e data de nascimento são obrigatórios.");
		}

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
		}

		usuario.setId(null);
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		Usuario salvo = usuarioRepository.save(usuario);
		return UsuarioPublicoDTO.de(salvo);
	}

	// Autenticação simples por e-mail/senha (sem AuthenticationManager):
	// evita depender do fluxo padrão do Spring Security pra permitir a
	// mesma mensagem de erro única do mock ("Usuário ou senha
	// inválidos.") tanto pra e-mail inexistente quanto pra senha errada.
	public UsuarioLogin autenticar(String emailDigitado, String senhaDigitada) {
		Usuario usuario = usuarioRepository.findByUsuario(emailDigitado)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos."));

		if (senhaDigitada == null || !passwordEncoder.matches(senhaDigitada, usuario.getSenha())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos.");
		}

		UsuarioLogin resposta = new UsuarioLogin();
		resposta.setId(usuario.getId());
		resposta.setNome(usuario.getNome());
		resposta.setNomeReal(usuario.getNomeReal());
		resposta.setNomeSocial(usuario.getNomeSocial());
		resposta.setComoChamar(usuario.getComoChamar());
		resposta.setUsuario(usuario.getUsuario());
		resposta.setCelular(usuario.getCelular());
		resposta.setFoto(usuario.getFoto());
		resposta.setGenero(usuario.getGenero());
		resposta.setDataNascimento(usuario.getDataNascimento());
		resposta.setIdade(usuario.getIdade());
		resposta.setSenha("");
		resposta.setToken("Bearer " + jwtService.generateToken(usuario.getUsuario()));
		return resposta;
	}

	// Atualização PARCIAL — só os campos enviados mudam. "senha" aqui não
	// troca a senha: é a senha ATUAL, pra confirmar que quem está editando
	// é o dono da conta (mesma regra do mock).
	public UsuarioPublicoDTO atualizar(AtualizarUsuarioRequest dados, Usuario autenticado) {
		Long idAlvo = dados.id() != null ? dados.id() : autenticado.getId();

		Usuario alvo = usuarioRepository.findById(idAlvo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

		if (!passwordEncoder.matches(Optional.ofNullable(dados.senha()).orElse(""), alvo.getSenha())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha atual incorreta.");
		}

		if (dados.nomeReal() != null) alvo.setNomeReal(dados.nomeReal());
		if (dados.nomeSocial() != null) alvo.setNomeSocial(dados.nomeSocial());
		if (dados.comoChamar() != null) alvo.setComoChamar(dados.comoChamar());
		if (dados.usuario() != null) alvo.setUsuario(dados.usuario());
		if (dados.celular() != null) alvo.setCelular(dados.celular());
		if (dados.foto() != null) alvo.setFoto(dados.foto());
		if (dados.genero() != null) alvo.setGenero(dados.genero());
		if (dados.dataNascimento() != null) alvo.setDataNascimento(dados.dataNascimento());

		Usuario salvo = usuarioRepository.save(alvo);
		return UsuarioPublicoDTO.de(salvo);
	}

	private boolean isBlank(String texto) {
		return texto == null || texto.isBlank();
	}
}
