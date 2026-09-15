package com.generation.carona_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.carona_api.model.Mensagem;
import com.generation.carona_api.model.Reserva;
import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.repository.MensagemRepository;

@Service
public class MensagemService {

	private final MensagemRepository mensagemRepository;
	private final ReservaService reservaService;
	private final SanitizacaoService sanitizacaoService;

	public MensagemService(MensagemRepository mensagemRepository, ReservaService reservaService,
			SanitizacaoService sanitizacaoService) {
		this.mensagemRepository = mensagemRepository;
		this.reservaService = reservaService;
		this.sanitizacaoService = sanitizacaoService;
	}

	public List<Mensagem> listar(Long reservaId, Usuario participante) {
		reservaService.buscarParaParticipante(reservaId, participante);
		return mensagemRepository.findByReserva_IdOrderByCriadoEmAsc(reservaId);
	}

	public Mensagem enviar(Long reservaId, String texto, Usuario autor) {
		Reserva reserva = reservaService.buscarParaParticipante(reservaId, autor);

		String textoLimpo = texto == null ? "" : texto.trim();
		if (textoLimpo.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escreva algo antes de enviar.");
		}

		// Telefones e palavrões são filtrados aqui, no back — antes de
		// qualquer pessoa (inclusive quem enviou) ver a mensagem salva.
		String sanitizado = sanitizacaoService.sanitizar(textoLimpo);
		if (sanitizado.length() > 1000) {
			sanitizado = sanitizado.substring(0, 1000);
		}

		Mensagem mensagem = new Mensagem();
		mensagem.setReserva(reserva);
		mensagem.setAutor(autor);
		mensagem.setTexto(sanitizado);
		return mensagemRepository.save(mensagem);
	}
}
