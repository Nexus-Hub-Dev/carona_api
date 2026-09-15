package com.generation.carona_api.controller;

import java.util.List;

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

import com.generation.carona_api.dto.VeiculoResumoDTO;
import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.Veiculo;
import com.generation.carona_api.repository.VeiculoRepository;
import com.generation.carona_api.security.AutenticacaoAtual;

import jakarta.validation.Valid;

// O controller original listava/editava/apagava veículo de QUALQUER
// usuário — sem checar dono. Aqui, cada operação é restrita a quem
// autenticou, igual ao mock.
@RestController
@RequestMapping("/veiculos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VeiculoController {

	@Autowired
	private VeiculoRepository veiculoRepository;

	@Autowired
	private AutenticacaoAtual autenticacaoAtual;

	@GetMapping
	public ResponseEntity<List<VeiculoResumoDetalhadoDTO>> getMeus() {
		Usuario autenticado = autenticacaoAtual.obter();
		List<VeiculoResumoDetalhadoDTO> meus = veiculoRepository.findAll().stream()
				.filter(v -> v.getUsuario() != null && v.getUsuario().getId().equals(autenticado.getId()))
				.map(VeiculoResumoDetalhadoDTO::de)
				.toList();
		return ResponseEntity.ok(meus);
	}

	@PostMapping
	public ResponseEntity<VeiculoResumoDetalhadoDTO> cadastrar(@Valid @RequestBody Veiculo veiculo) {
		Usuario autenticado = autenticacaoAtual.obter();
		veiculo.setId(null);
		veiculo.setUsuario(autenticado);
		Veiculo salvo = veiculoRepository.save(veiculo);
		return ResponseEntity.status(HttpStatus.CREATED).body(VeiculoResumoDetalhadoDTO.de(salvo));
	}

	@PutMapping
	public ResponseEntity<VeiculoResumoDetalhadoDTO> atualizar(@Valid @RequestBody Veiculo veiculo) {
		Usuario autenticado = autenticacaoAtual.obter();
		Veiculo alvo = veiculoRepository.findById(veiculo.getId())
				.filter(v -> v.getUsuario() != null && v.getUsuario().getId().equals(autenticado.getId()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado."));

		alvo.setModelo(veiculo.getModelo());
		alvo.setPlaca(veiculo.getPlaca());
		alvo.setCor(veiculo.getCor());
		alvo.setFoto(veiculo.getFoto());
		alvo.setCapacidade(veiculo.getCapacidade());
		alvo.setAcessivelPcd(veiculo.getAcessivelPcd());

		return ResponseEntity.ok(VeiculoResumoDetalhadoDTO.de(veiculoRepository.save(alvo)));
	}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void excluir(@PathVariable Long id) {
		Usuario autenticado = autenticacaoAtual.obter();
		Veiculo alvo = veiculoRepository.findById(id)
				.filter(v -> v.getUsuario() != null && v.getUsuario().getId().equals(autenticado.getId()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado."));
		veiculoRepository.delete(alvo);
	}

	// Recorte de Veiculo enviado ao front: igual ao veiculoPublico() do
	// mock — nunca inclui o id do dono nem a lista de viagens.
	public record VeiculoResumoDetalhadoDTO(
			Long id, String modelo, String placa, String foto, String cor, Integer capacidade, Boolean acessivelPcd) {

		public static VeiculoResumoDetalhadoDTO de(Veiculo veiculo) {
			return new VeiculoResumoDetalhadoDTO(
					veiculo.getId(), veiculo.getModelo(), veiculo.getPlaca(), veiculo.getFoto(),
					veiculo.getCor(), veiculo.getCapacidade(), veiculo.getAcessivelPcd());
		}
	}
}
