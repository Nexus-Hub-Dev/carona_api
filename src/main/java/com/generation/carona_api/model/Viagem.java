package com.generation.carona_api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_viagem")
public class Viagem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O atributo partida é obrigatório!")
	@Size(min = 3, max = 80, message = "O atributo partida deve ter no mínimo 3 e no máximo 80 caracteres")
	@Column(length = 80)
	private String partida;

	@NotBlank(message = "O atributo destino é obrigatório!")
	@Size(min = 3, max = 80, message = "O atributo destino deve ter no mínimo 3 e no máximo 80 caracteres")
	@Column(length = 80)
	private String destino;

	@FutureOrPresent(message = "A data deve ser futura ou presente")
	@NotNull
	private LocalDateTime data;

	//@Min(value = 0, message = "A distância não pode ser negativa")
	//@NotNull(message = "O atributo distanciaKm é obrigatório!")
	@Column
	private Double distanciaKm;

	//@Min(value = 0, message = "O tempo estimado não pode ser negativo")
	//@NotNull(message = "O atributo tempoEstimadoMin é obrigatório!")
	@Column
	private Double tempoEstimadoMin;

	//@Min(value = 0, message = "O valor da viagem não pode ser negativo")
	//@NotNull(message = "O atributo valorKm é obrigatório!")
	@Column
	private Double valorKm;

	//@Min(value = 0, message = "A velocidade média não pode ser negativa")
	//@NotNull(message = "O atributo velocidadeMedia é obrigatório!")
	@Column
	private Integer velocidadeMedia;

	//@Min(value = -90, message = "A latitude de partida mínima é -90")
	//@Max(value = 90, message = "A latitude de partida máxima é 90")
	//@NotNull(message = "A latitude de partida é obrigatória!")
	@Column
	private Double latitudePartida;

	//@Min(value = -90, message = "A latitude de destino mínima é -90")
	//@Max(value = 90, message = "A latitude de destino máxima é 90")
	//@NotNull(message = "A latitude de destino é obrigatória!")
	@Column
	private Double latitudeDestino;

	//@Min(value = -180, message = "A longitude de partida mínima é -180")
	//@Max(value = 180, message = "A longitude de partida máxima é 180")
	//@NotNull(message = "A longitude de partida é obrigatória!")
	@Column
	private Double longitudePartida;

	//@Min(value = -180, message = "A longitude de destino mínima é -180")
	//@Max(value = 180, message = "A longitude de destino máxima é 180")
	//@NotNull(message = "A longitude de destino é obrigatória!")
	@Column
	private Double longitudeDestino;
	
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	@JsonIgnoreProperties(value = "viagem", allowSetters = true)
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "veiculo_id")
	@JsonIgnoreProperties(value = "viagem", allowSetters = true)
	private Veiculo veiculo;

	// --- Getters e Setters ---

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

	public Double getDistanciaKm() {
		return distanciaKm;
	}

	public void setDistanciaKm(Double distanciaKm) {
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

	public Integer getVelocidadeMedia() {
		return velocidadeMedia;
	}

	public void setVelocidadeMedia(Integer velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}

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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}
}