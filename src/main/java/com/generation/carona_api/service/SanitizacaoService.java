package com.generation.carona_api.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

// Porta em Java do mesmo filtro do mock-server (sanitizarMensagem):
// números de telefone brasileiros e uma lista curta de palavrões viram
// "####"/"#####" antes de a mensagem do chat ser salva.
@Service
public class SanitizacaoService {

	// Cobre os formatos mais comuns de telefone brasileiro digitados em texto
	// livre: com/sem DDD entre parênteses, com/sem hífen, com/sem +55, celular
	// (9 dígitos) ou fixo (8 dígitos).
	private static final Pattern REGEX_TELEFONE = Pattern
			.compile("(\\+?55[\\s.-]?)?\\(?\\d{2}\\)?[\\s.-]?9?\\d{4}[\\s.-]?\\d{4}");

	// Lista curta e representativa de baixo calão em português — o objetivo
	// aqui é demonstrar o mecanismo de filtro, não ser um dicionário
	// exaustivo de moderação de conteúdo.
	private static final List<String> PALAVROES = List.of(
			"porra", "caralho", "merda", "puta", "putas", "puto", "putos",
			"bosta", "cacete", "foda-se", "fodase", "viado", "arrombado", "arrombada",
			"desgraça", "desgracado", "desgraçado", "filho da puta", "fdp");

	private static final Pattern REGEX_PALAVROES = Pattern.compile(
			"\\b(" + String.join("|", PALAVROES.stream().map(Pattern::quote).toList()) + ")\\b",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	public String sanitizar(String texto) {
		if (texto == null) return "";

		String semTelefone = REGEX_TELEFONE.matcher(texto).replaceAll("####");

		Matcher matcher = REGEX_PALAVROES.matcher(semTelefone);
		return matcher.replaceAll(resultado -> "#".repeat(resultado.group().length()));
	}
}
