package com.generation.carona_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.carona_api.model.Reserva;
import com.generation.carona_api.model.StatusReserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	// Solicitações que EU fiz como passageiro/a.
	List<Reserva> findByPassageiro_IdOrderByCriadoEmDesc(Long passageiroId);

	// Solicitações recebidas nas caronas que EU ofereço (dono da viagem).
	List<Reserva> findByViagem_Usuario_IdOrderByCriadoEmDesc(Long usuarioId);

	long countByViagem_IdAndStatus(Long viagemId, StatusReserva status);

	long countByPassageiro_IdAndStatus(Long passageiroId, StatusReserva status);

	boolean existsByViagem_IdAndPassageiro_IdAndStatusIn(Long viagemId, Long passageiroId, List<StatusReserva> status);

	// Outras solicitações pendentes do MESMO passageiro — canceladas
	// automaticamente quando uma delas é aceita (regra do mock).
	List<Reserva> findByPassageiro_IdAndStatusAndIdNot(Long passageiroId, StatusReserva status, Long idExcluido);
}
