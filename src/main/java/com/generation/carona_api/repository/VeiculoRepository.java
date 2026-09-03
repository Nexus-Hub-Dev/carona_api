package com.generation.carona_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.carona_api.model.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
	public List<Veiculo> findAllByModeloContainingIgnoreCase(String modelo);
	public Optional<Veiculo> findByPlacaIgnoreCase(String placa);
	public List<Veiculo> findAllByAcessivelPcdTrue();
}