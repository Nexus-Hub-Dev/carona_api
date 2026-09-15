package com.generation.carona_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.carona_api.model.Mensagem;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

	List<Mensagem> findByReserva_IdOrderByCriadoEmAsc(Long reservaId);
}
