package com.generation.carona_api.dto;

import java.time.LocalDateTime;

// Atualização parcial de viagem (PUT /viagens): só os campos não nulos
// são aplicados sobre a viagem existente — igual ao Object.assign do
// mock. Sem isso, um PUT que só manda {id, partida, destino, ...} (como
// ResultadosCaronas.tsx faz) apagaria os campos omitidos.
public record ViagemUpdateRequest(
		Long id,
		String partida,
		String destino,
		String bairroDestino,
		LocalDateTime data,
		Boolean disponivelPCD,
		Boolean apenasMulheres,
		Boolean aceitaPet,
		Double valorSugerido,
		Double valorTotal,
		Integer vagasDisponiveis,
		VeiculoRefRequest veiculo) {

	public record VeiculoRefRequest(Long id) {
	}
}
