package com.generation.carona_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_viagem")
public class Viagem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O atributo tipo titulo é obrigatório!")
	@Size(min = 3, max = 80, message = "O atributo tipo deve ter no mínimo 3 e no máximo 80 caracter obrigatório")
	@Column(length = 80)
	private String partida;

	@NotBlank(message = "O atributo tipo titulo é obrigatório!")
	@Size(min = 3, max = 80, message = "O atributo tipo deve ter no mínimo 3 e no máximo 80 caracter obrigatório")
	@Column(length = 80)
	private String destino;

	@FutureOrPresent(message = "A data deve ser futura")
	@NotNull
	private LocalDateTime data;

	@PositiveOrZero
	@NotNull(message = "O atributo valorKm é obrigatório!")
	@Column(precision = 4, scale = 2)
	private BigDecimal distanciaKm;

	@PositiveOrZero
	@NotNull(message = "O atributo distanciaKm é obrigatório!")
	@Column(precision = 7, scale = 2)
	private Double tempoEstimadoMin;

	@PositiveOrZero
	@NotNull(message = "O atributo distanciaKm é obrigatório!")
	@Column(precision = 7, scale = 2)
	private Double valorKm;

	@PositiveOrZero
	@NotNull(message = "O atributo velocidadeMedia é obrigatório!")
	@Column
	private Integer velocidadeMedia;

	@PositiveOrZero
	@NotNull(message = "A latitude de partida é obrigatória!")
	@Column(precision = 9, scale = 6)
	private Double latitudePartida;

	@PositiveOrZero
	@NotNull(message = "A latitude de destino é obrigatória!")
	@Column(precision = 9, scale = 6)
	private Double latitudeDestino;

	@PositiveOrZero
	@NotNull(message = "A longitude de partida é obrigatória!")
	@Column(precision = 9, scale = 6)
	private Double longitudePartida;

	@PositiveOrZero
	@NotNull(message = "A longitude de destino é obrigatória!")
	@Column(precision = 9, scale = 6)
	private Double longitudeDestino;
	

	@ManyToOne
	@Column(name = "usuario_id")
	@JsonIgnoreProperties(value = "viagem", allowSetters = true)
	private List<Usuario> usuario;

	@ManyToOne
	@Column(name = "veiculo_id")
	@JsonIgnoreProperties(value = "viagem", allowSetters = true)
	private List<Veiculo> veiculo;

	public Double getLatitudePartida() {
		return latitudePartida;
	}

	public void setLatitudePartida(Double latitudePartida) {
		this.latitudePartida = latitudePartida;
	}

	public Double getLatitudeDestino() {
		return latitudeDestino;
	}

	public void setLatitudeDestino(Double latitudeDestino) {
		this.latitudeDestino = latitudeDestino;
	}

	public Double getLongitudePartida() {
		return longitudePartida;
	}

	public void setLongitudePartida(Double longitudePartida) {
		this.longitudePartida = longitudePartida;
	}

	public Double getLongitudeDestino() {
		return longitudeDestino;
	}

	public void setLongitudeDestino(Double longitudeDestino) {
		this.longitudeDestino = longitudeDestino;
	}

	public Integer getVelocidadeMedia() {
		return velocidadeMedia;
	}

	public void setVelocidadeMedia(Integer velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPartida() {
		return partida;
	}

	public void setPartida(String partida) {
		this.partida = partida;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	public BigDecimal getDistanciaKm() {
		return distanciaKm;
	}

	public void setDistanciaKm(BigDecimal distanciaKm) {
		this.distanciaKm = distanciaKm;
	}

	public Double getTempoEstimadoMin() {
		return tempoEstimadoMin;
	}

	public void setTempoEstimadoMin(Double tempoEstimadoMin) {
		this.tempoEstimadoMin = tempoEstimadoMin;
	}

	public Double getValorKm() {
		return valorKm;
	}

	public void setValorKm(Double valorKm) {
		this.valorKm = valorKm;
	}

	public List<Usuario> getUsuario() {
		return usuario;
	}

	public void setUsuario(List<Usuario> usuario) {
		this.usuario = usuario;
	}

	public List<Veiculo> getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(List<Veiculo> veiculo) {
		this.veiculo = veiculo;
	}

}
