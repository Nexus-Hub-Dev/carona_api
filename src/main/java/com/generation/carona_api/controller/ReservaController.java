package com.generation.carona_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.carona_api.dto.EnviarMensagemRequest;
import com.generation.carona_api.dto.MensagemResponseDTO;
import com.generation.carona_api.dto.ReservaResponseDTO;
import com.generation.carona_api.dto.SolicitarReservaRequest;
import com.generation.carona_api.model.Mensagem;
import com.generation.carona_api.model.Reserva;
import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.security.AutenticacaoAtual;
import com.generation.carona_api.service.MensagemService;
import com.generation.carona_api.service.ReservaService;

import jakarta.validation.Valid;

// Solicitação de vaga numa carona (Solicitar/Minhas solicitações/
// Aceitar/Recusar/Cancelar) + o chat vinculado a cada reserva — o maior
// pedaço que faltava no back original em relação ao mock.
@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReservaController {

	private final ReservaService reservaService;
	private final MensagemService mensagemService;
	private final AutenticacaoAtual autenticacaoAtual;

	public ReservaController(ReservaService reservaService, MensagemService mensagemService,
			AutenticacaoAtual autenticacaoAtual) {
		this.reservaService = reservaService;
		this.mensagemService = mensagemService;
		this.autenticacaoAtual = autenticacaoAtual;
	}

	private ReservaResponseDTO paraDTO(Reserva reserva) {
		return ReservaResponseDTO.de(reserva, reservaService.vagasRestantes(reserva.getViagem()));
	}

	@PostMapping
	public ResponseEntity<ReservaResponseDTO> solicitar(@Valid @RequestBody SolicitarReservaRequest dados) {
		Usuario autenticado = autenticacaoAtual.obter();
		Reserva reserva = reservaService.solicitar(dados.viagemId(), autenticado);
		return ResponseEntity.status(HttpStatus.CREATED).body(paraDTO(reserva));
	}

	@GetMapping("/minhas")
	public ResponseEntity<List<ReservaResponseDTO>> minhas() {
		Usuario autenticado = autenticacaoAtual.obter();
		List<ReservaResponseDTO> minhas = reservaService.minhas(autenticado).stream().map(this::paraDTO).toList();
		return ResponseEntity.ok(minhas);
	}

	@GetMapping("/recebidas")
	public ResponseEntity<List<ReservaResponseDTO>> recebidas() {
		Usuario autenticado = autenticacaoAtual.obter();
		List<ReservaResponseDTO> recebidas = reservaService.recebidas(autenticado).stream().map(this::paraDTO).toList();
		return ResponseEntity.ok(recebidas);
	}

	@PutMapping("/{id}/aceitar")
	public ResponseEntity<ReservaResponseDTO> aceitar(@PathVariable Long id) {
		Usuario autenticado = autenticacaoAtual.obter();
		return ResponseEntity.ok(paraDTO(reservaService.aceitar(id, autenticado)));
	}

	@PutMapping("/{id}/recusar")
	public ResponseEntity<ReservaResponseDTO> recusar(@PathVariable Long id) {
		Usuario autenticado = autenticacaoAtual.obter();
		return ResponseEntity.ok(paraDTO(reservaService.recusar(id, autenticado)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
		Usuario autenticado = autenticacaoAtual.obter();
		return ResponseEntity.ok(paraDTO(reservaService.cancelar(id, autenticado)));
	}

	// ---------------- Chat da reserva ----------------

	@GetMapping("/{id}/mensagens")
	public ResponseEntity<List<MensagemResponseDTO>> listarMensagens(@PathVariable Long id) {
		Usuario autenticado = autenticacaoAtual.obter();
		List<MensagemResponseDTO> mensagens = mensagemService.listar(id, autenticado).stream()
				.map(MensagemResponseDTO::de)
				.toList();
		return ResponseEntity.ok(mensagens);
	}

	@PostMapping("/{id}/mensagens")
	public ResponseEntity<MensagemResponseDTO> enviarMensagem(@PathVariable Long id,
			@RequestBody EnviarMensagemRequest dados) {
		Usuario autenticado = autenticacaoAtual.obter();
		Mensagem mensagem = mensagemService.enviar(id, dados.texto(), autenticado);
		return ResponseEntity.status(HttpStatus.CREATED).body(MensagemResponseDTO.de(mensagem));
	}
}
