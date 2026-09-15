package com.generation.carona_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.generation.carona_api.model.Reserva;
import com.generation.carona_api.model.StatusReserva;
import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.Viagem;
import com.generation.carona_api.repository.ReservaRepository;
import com.generation.carona_api.repository.ViagemRepository;

// Porta em Java das regras de negócio do mock (bloco "Reservas" do
// mock-server.mjs) — a peça que não existia no back original.
@Service
public class ReservaService {

	// Máximo de solicitações pendentes simultâneas por passageiro/a —
	// mesmo limite do mock.
	private static final int LIMITE_RESERVAS_PENDENTES = 3;

	private final ReservaRepository reservaRepository;
	private final ViagemRepository viagemRepository;
	private final ViagemService viagemService;

	public ReservaService(ReservaRepository reservaRepository, ViagemRepository viagemRepository,
			ViagemService viagemService) {
		this.reservaRepository = reservaRepository;
		this.viagemRepository = viagemRepository;
		this.viagemService = viagemService;
	}

	public int vagasRestantes(Viagem viagem) {
		return viagemService.calcularVagasRestantes(viagem);
	}

	@Transactional
	public Reserva solicitar(Long viagemId, Usuario passageiro) {
		Viagem viagem = viagemRepository.findById(viagemId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carona não encontrada."));

		if (viagem.getUsuario().getId().equals(passageiro.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Você não pode solicitar uma vaga na sua própria carona.");
		}

		boolean jaSolicitou = reservaRepository.existsByViagem_IdAndPassageiro_IdAndStatusIn(
				viagemId, passageiro.getId(), List.of(StatusReserva.pendente, StatusReserva.aceita));
		if (jaSolicitou) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Você já tem uma solicitação em andamento para esta carona.");
		}

		long pendentesDoPassageiro = reservaRepository.countByPassageiro_IdAndStatus(passageiro.getId(), StatusReserva.pendente);
		if (pendentesDoPassageiro >= LIMITE_RESERVAS_PENDENTES) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já tem " + LIMITE_RESERVAS_PENDENTES
					+ " solicitações pendentes. Aguarde uma resposta ou cancele alguma antes de pedir outra.");
		}

		if (vagasRestantes(viagem) <= 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Não há mais vagas disponíveis nesta carona.");
		}

		Reserva reserva = new Reserva();
		reserva.setViagem(viagem);
		reserva.setPassageiro(passageiro);
		reserva.setStatus(StatusReserva.pendente);
		return reservaRepository.save(reserva);
	}

	public List<Reserva> minhas(Usuario passageiro) {
		return reservaRepository.findByPassageiro_IdOrderByCriadoEmDesc(passageiro.getId());
	}

	public List<Reserva> recebidas(Usuario motorista) {
		return reservaRepository.findByViagem_Usuario_IdOrderByCriadoEmDesc(motorista.getId());
	}

	@Transactional
	public Reserva aceitar(Long reservaId, Usuario motorista) {
		Reserva reserva = buscarComoMotorista(reservaId, motorista);

		if (reserva.getStatus() != StatusReserva.pendente) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta solicitação já foi respondida.");
		}
		if (vagasRestantes(reserva.getViagem()) <= 0) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Não há mais vagas disponíveis nesta carona.");
		}

		reserva.setStatus(StatusReserva.aceita);
		reserva.setAtualizadoEm(LocalDateTime.now());
		Reserva salva = reservaRepository.save(reserva);

		// Regra de negócio: ao aceitar uma solicitação, as outras
		// pendentes do MESMO passageiro (em quaisquer outras caronas) são
		// canceladas automaticamente — ele só fica em uma carona
		// confirmada por vez.
		List<Reserva> outrasPendentes = reservaRepository.findByPassageiro_IdAndStatusAndIdNot(
				reserva.getPassageiro().getId(), StatusReserva.pendente, reserva.getId());
		for (Reserva outra : outrasPendentes) {
			outra.setStatus(StatusReserva.cancelada);
			outra.setMotivo("Cancelada automaticamente: outra solicitação foi aceita.");
			outra.setAtualizadoEm(LocalDateTime.now());
			reservaRepository.save(outra);
		}

		return salva;
	}

	@Transactional
	public Reserva recusar(Long reservaId, Usuario motorista) {
		Reserva reserva = buscarComoMotorista(reservaId, motorista);

		if (reserva.getStatus() != StatusReserva.pendente) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta solicitação já foi respondida.");
		}

		reserva.setStatus(StatusReserva.recusada);
		reserva.setAtualizadoEm(LocalDateTime.now());
		return reservaRepository.save(reserva);
	}

	@Transactional
	public Reserva cancelar(Long reservaId, Usuario passageiro) {
		Reserva reserva = reservaRepository.findById(reservaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada."));

		if (!reserva.getPassageiro().getId().equals(passageiro.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode cancelar a solicitação de outra pessoa.");
		}
		if (reserva.getStatus() != StatusReserva.pendente) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Só é possível cancelar solicitações pendentes.");
		}

		reserva.setStatus(StatusReserva.cancelada);
		reserva.setMotivo("Cancelada pelo passageiro.");
		reserva.setAtualizadoEm(LocalDateTime.now());
		return reservaRepository.save(reserva);
	}

	// Usado também pelo chat: só quem pediu a vaga ou quem dirige a
	// viagem participa da conversa.
	public Reserva buscarParaParticipante(Long reservaId, Usuario participante) {
		Reserva reserva = reservaRepository.findById(reservaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada."));

		boolean ehPassageiro = reserva.getPassageiro().getId().equals(participante.getId());
		boolean ehMotorista = reserva.getViagem().getUsuario().getId().equals(participante.getId());
		if (!ehPassageiro && !ehMotorista) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não participa dessa conversa.");
		}
		return reserva;
	}

	private Reserva buscarComoMotorista(Long reservaId, Usuario motorista) {
		Reserva reserva = reservaRepository.findById(reservaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação não encontrada."));

		if (!reserva.getViagem().getUsuario().getId().equals(motorista.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não é a pessoa motorista desta carona.");
		}
		return reserva;
	}
}
