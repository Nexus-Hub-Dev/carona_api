package com.generation.carona_api.dto;

import jakarta.validation.constraints.NotBlank;

public class SugestaoValorRequest {

    @NotBlank(message = "O endereço de partida é obrigatório")
    private String partida;

    @NotBlank(message = "O endereço de destino é obrigatório")
    private String destino;

    public String getPartida() { return partida; }
    public void setPartida(String partida) { this.partida = partida; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
}