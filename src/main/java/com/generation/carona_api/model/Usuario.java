package com.generation.carona_api.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Identidade: três campos de nome em vez de um só "nome" — nomeReal é
	// o nome verdadeiro; nomeSocial e comoChamar são opcionais. O nome de
	// exibição (usado em toda a UI) é calculado, nunca armazenado — ver
	// getNome().
	@NotBlank(message = "O Atributo Nome é Obrigatório!")
	@Column(length = 255)
	private String nomeReal;

	@Column(length = 255)
	private String nomeSocial;

	@Column(length = 255)
	private String comoChamar;

	@NotBlank(message = "O Atributo celular é Obrigatório!")
	// O front envia o celular formatado ("(11) 91234-5678"), não só
	// dígitos — por isso sem @Size fixo e com coluna mais larga.
	@Column(length = 20)
	private String celular;

	@NotBlank(message = "O Atributo Usuário é Obrigatório!")
	@Email(message = "O Atributo Usuário deve ser um email válido!")
	@Column(length = 255)
	private String usuario;

	@NotBlank(message = "O Atributo Senha é Obrigatório!")
	@Size(min = 8, message = "A Senha deve ter no mínimo 8 caracteres")
	@Column(length = 255)
	private String senha;

	@Size(max = 5000, message = "O link da foto não pode ser maior do que 5000 caracteres")
	@Column(length = 5000)
	private String foto;

	@NotBlank(message = "O Atributo Gênero é Obrigatório!")
	@Column(length = 50)
	private String genero;

	// Usada só para calcular a idade (getIdade()) — não há regra de
	// negócio no back que dependa diretamente da data em si.
	private LocalDate dataNascimento;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties(value = "usuario", allowSetters = true)
	private List<Viagem> viagem;

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeReal() {
		return nomeReal;
	}

	public void setNomeReal(String nomeReal) {
		this.nomeReal = nomeReal;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public String getComoChamar() {
		return comoChamar;
	}

	public void setComoChamar(String comoChamar) {
		this.comoChamar = comoChamar;
	}

	// Nome de exibição: comoChamar, senão nomeSocial, senão nomeReal —
	// mesma prioridade usada no mock (nomeExibicao) e no restante do app.
	@Transient
	public String getNome() {
		if (comoChamar != null && !comoChamar.isBlank()) return comoChamar;
		if (nomeSocial != null && !nomeSocial.isBlank()) return nomeSocial;
		return nomeReal;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	@Transient
	public Integer getIdade() {
		if (dataNascimento == null) return null;
		return Period.between(dataNascimento, LocalDate.now()).getYears();
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public List<Viagem> getViagem() {
		return viagem;
	}

	public void setViagem(List<Viagem> viagem) {
		this.viagem = viagem;
	}
}
