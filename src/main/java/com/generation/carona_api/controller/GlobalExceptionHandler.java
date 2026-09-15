package com.generation.carona_api.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// O mock (e o front, construído em cima dele) sempre respondem erro como
// {"message": "..."}; o Spring por padrão devolve um ProblemDetail
// ({"type","title","status","detail"...}) ou uma lista de campos em
// erro — nenhum dos dois tem "message". Este handler central normaliza
// toda exceção pra sempre sair no formato que o front já sabe ler.
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
		return ResponseEntity.status(ex.getStatusCode())
				.body(Map.of("message", ex.getReason() != null ? ex.getReason() : "Requisição inválida."));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("message", "Não autenticado."));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		List<Map<String, String>> erros = ex.getBindingResult().getFieldErrors().stream()
				.map(erro -> {
					Map<String, String> item = new LinkedHashMap<>();
					item.put("field", erro.getField());
					item.put("defaultMessage", erro.getDefaultMessage());
					return item;
				})
				.toList();

		String primeiraMensagem = erros.isEmpty() ? "Dados inválidos." : erros.get(0).get("defaultMessage");

		Map<String, Object> corpo = new LinkedHashMap<>();
		corpo.put("message", primeiraMensagem);
		corpo.put("errors", erros);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGenerico(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("message", "Não foi possível concluir a requisição."));
	}
}
