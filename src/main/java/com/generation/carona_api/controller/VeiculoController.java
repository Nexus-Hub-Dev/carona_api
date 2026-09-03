package com.generation.carona_api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.carona_api.model.Veiculo;
import com.generation.carona_api.repository.VeiculoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/veiculos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VeiculoController {
	@Autowired
	private VeiculoRepository veiculoRepository;

	@GetMapping
	public ResponseEntity<List<Veiculo>> getAll() {
		return ResponseEntity.ok(veiculoRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Veiculo> getById(@PathVariable Long id) {
		return veiculoRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/modelo/{modelo}")
	public ResponseEntity<List<Veiculo>> getAllByModelo(@PathVariable String modelo) {
		return ResponseEntity.ok(veiculoRepository.findAllByModeloContainingIgnoreCase(modelo));
	}
	
	@GetMapping("/placa/{placa}")
	public ResponseEntity<Veiculo> getByPlaca(@PathVariable String placa) {
		return veiculoRepository.findByPlacaIgnoreCase(placa)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/pcd")
	public ResponseEntity<List<Veiculo>> getByAcessivelPcd() {
		return ResponseEntity.ok(veiculoRepository.findAllByAcessivelPcdTrue());
	}

	@PostMapping
	public ResponseEntity<Veiculo> post(@Valid @RequestBody Veiculo Veiculo) {
		return ResponseEntity.status(HttpStatus.CREATED).body(veiculoRepository.save(Veiculo));
	}

	@PutMapping
	public ResponseEntity<Veiculo> put(@Valid @RequestBody Veiculo Veiculo) {
		if (veiculoRepository.existsById(Veiculo.getId()))
			return ResponseEntity.ok(veiculoRepository.save(Veiculo));

		return ResponseEntity.notFound().build();
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Veiculo> Veiculo = veiculoRepository.findById(id);

		if (Veiculo.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);

		veiculoRepository.deleteById(id);
	}
}