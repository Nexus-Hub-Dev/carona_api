package com.generation.carona_api.repository;

import com.generation.carona_api.model.Viagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    List<Viagem> findAllByPartidaContainingIgnoreCase(@Param("partida") String partida);

    List<Viagem> findAllByDestinoContainingIgnoreCase(@Param("destino") String destino);
    
    List<Viagem> findBySomenteMulheresTrue();
    
    List<Viagem> findByDisponivelPCDTrue();
}