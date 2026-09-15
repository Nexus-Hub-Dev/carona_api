package com.generation.carona_api.dto;

import com.generation.carona_api.model.Veiculo;

public record VeiculoResumoDTO(Long id, String modelo, String placa) {

	public static VeiculoResumoDTO de(Veiculo veiculo) {
		if (veiculo == null) return null;
		return new VeiculoResumoDTO(veiculo.getId(), veiculo.getModelo(), veiculo.getPlaca());
	}
}
