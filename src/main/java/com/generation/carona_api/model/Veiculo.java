package com.generation.carona_api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_veiculos")
public class Veiculo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O atributo modelo é obrigatório!")
	@Size(min = 5, max = 100, message = "O modelo deve ter no mínimo 5 e no máximo 100 caracteres")
	@Column(length = 100)
	private String modelo;

	@Pattern(regexp = "^[A-Z]{3}-?[0-9]{4}$|^[A-Z]{3}-?[0-9][A-Z][0-9]{2}$", message = "Informe uma placa válida. Formatos aceitos: ABC1234 ou ABC1D34.")
	@NotBlank(message = "O atributo placa é obrigatório!")
	@Size(min = 7, max = 7, message = "O atributo placa deve conter exatamente 7 dígitos e não pode conter caracteres especiais!")
	@Column(unique = true, length = 7)
	private String placa;

	@Size(max = 5000, message = "o atributo foto deve conter no máximo 5000 caracteres")
	@Column(length = 5000)
	private String foto;

	@NotBlank(message = "O atributo cor é obrigatório!")
	@Size(min = 3, max = 100, message = "O atributo cor deve ter no mínimo 3 e no máximo 100 caracteres")
	@Column(length = 100)
	private String cor;

	@NotNull(message = "O atributo capacidade é obrigatório! Digite quantos lugares disponíveis o carro tem")
	private int capacidade;
	
	//M: Criação do atributo acessivelPcd
	@NotNull(message = "O atributo acessivelPcd é obrigatório!")
	private Boolean acessivelPcd;

	@OneToMany
	@JsonIgnoreProperties("veiculo")
	private List<Viagem> viagem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public int getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}

	public List<Viagem> getViagem() {
		return viagem;
	}

	public void setViagem(List<Viagem> viagem) {
		this.viagem = viagem;
	}

	//M: Getters e Setters - acessivelPcd 
	public Boolean getAcessivelPcd() {
		return acessivelPcd;
	}

	public void setAcessivelPcd(Boolean acessivelPcd) {
		this.acessivelPcd = acessivelPcd;
	}
	

	
	
	
}
