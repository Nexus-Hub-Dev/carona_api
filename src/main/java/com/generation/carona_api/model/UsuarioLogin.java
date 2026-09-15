package com.generation.carona_api.model;

// Corpo de requisição do login (usuario + senha) E corpo de resposta
// (preenchido com os dados públicos do usuário autenticado + token).
public class UsuarioLogin {

	private Long id;
	private String nome;
	private String nomeReal;
	private String nomeSocial;
	private String comoChamar;
	private String usuario;
	private String senha;
	private String celular;
	private String foto;
	private String genero;
	private java.time.LocalDate dataNascimento;
	private Integer idade;
	private String token;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
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
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
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
	public java.time.LocalDate getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(java.time.LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
