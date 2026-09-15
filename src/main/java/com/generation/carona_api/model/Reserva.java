package com.generation.carona_api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Solicitação de vaga numa carona — passageiro pede, motorista aceita ou
// recusa. Não existia no back original (só no mock); é a peça que faltava
// para o fluxo "Solicitar carona" / "Minhas solicitações" funcionar de
// verdade.
@Entity
@Table(name = "tb_reserva")
public class Reserva {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "viagem_id")
	@JsonIgnoreProperties(value = { "usuario", "veiculo" }, allowSetters = true)
	private Viagem viagem;

	@ManyToOne
	@JoinColumn(name = "passageiro_id")
	private Usuario passageiro;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private StatusReserva status = StatusReserva.pendente;

	@Column(length = 255)
	private String motivo;

	private LocalDateTime criadoEm = LocalDateTime.now();

	private LocalDateTime atualizadoEm = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Viagem getViagem() {
		return viagem;
	}

	public void setViagem(Viagem viagem) {
		this.viagem = viagem;
	}

	public Usuario getPassageiro() {
		return passageiro;
	}

	public void setPassageiro(Usuario passageiro) {
		this.passageiro = passageiro;
	}

	public StatusReserva getStatus() {
		return status;
	}

	public void setStatus(StatusReserva status) {
		this.status = status;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(LocalDateTime criadoEm) {
		this.criadoEm = criadoEm;
	}

	public LocalDateTime getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(LocalDateTime atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}
}
