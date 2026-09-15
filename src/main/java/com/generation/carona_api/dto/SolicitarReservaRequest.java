package com.generation.carona_api.dto;

import jakarta.validation.constraints.NotNull;

public record SolicitarReservaRequest(@NotNull(message = "Informe a viagem.") Long viagemId) {
}
