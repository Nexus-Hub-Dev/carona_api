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

import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.Veiculo;
import com.generation.carona_api.model.Viagem;
import com.generation.carona_api.repository.UsuarioRepository;
import com.generation.carona_api.repository.VeiculoRepository;
import com.generation.carona_api.repository.ViagemRepository;
import com.generation.carona_api.service.ViagemMapsService;
import com.generation.carona_api.service.ViagemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/viagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ViagemController {

	@Autowired
	private ViagemRepository viagemRepository;

	@Autowired
	private ViagemService viagemService;

	private final ViagemMapsService viagemMapsService;

	private UsuarioRepository usuarioRepository;

	private VeiculoRepository veiculoRepository;

	public ViagemController(ViagemRepository viagemRepository, ViagemService viagemService,
			ViagemMapsService viagemMapsService, UsuarioRepository usuarioRepository,
			VeiculoRepository veiculoRepository) {
		this.viagemRepository = viagemRepository;
		this.viagemService = viagemService;
		this.viagemMapsService = viagemMapsService;
		this.usuarioRepository = usuarioRepository;
		this.veiculoRepository = veiculoRepository;
	}

	@GetMapping
	public ResponseEntity<List<Viagem>> getAll() {
		return ResponseEntity.ok(viagemRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Viagem> getById(@PathVariable Long id) {
		return viagemRepository.findById(id).map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@GetMapping("/partida/{partida}")
	public ResponseEntity<List<Viagem>> getByPartida(@PathVariable String partida) {
		return ResponseEntity.ok(viagemRepository.findAllByPartidaContainingIgnoreCase(partida));
	}

	@GetMapping("/destino/{destino}")
	public ResponseEntity<List<Viagem>> getByDestino(@PathVariable String destino) {
		return ResponseEntity.ok(viagemRepository.findAllByDestinoContainingIgnoreCase(destino));
	}

	@GetMapping("/mulheres")
	public ResponseEntity<List<Viagem>> getByApenasMulheres() {
		return ResponseEntity.ok(viagemRepository.findAllByApenasMulheresTrue());
	}

	@GetMapping("/pcd")
	public ResponseEntity<List<Viagem>> getByAcessivelPcd() {
		return ResponseEntity.ok(viagemRepository.findAllByVeiculoAcessivelPcdTrue());
	}

	@PostMapping
	public ResponseEntity<Viagem> cadastrar(@Valid @RequestBody Viagem viagem) {
		if (viagem.getUsuario() == null || viagem.getUsuario().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id do usuário");
		}
		if (viagem.getVeiculo() == null || viagem.getVeiculo().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id do veículo");
		}

		Usuario usuarioCompleto = usuarioRepository.findById(viagem.getUsuario().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não encontrado"));
		Veiculo veiculoCompleto = veiculoRepository.findById(viagem.getVeiculo().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veículo não encontrado"));

		viagem.setUsuario(usuarioCompleto);
		viagem.setVeiculo(veiculoCompleto);

		try {
			viagemMapsService.preencherDadosRota(viagem);
		} catch (RuntimeException ex) {
			// O cadastro não deve falhar quando um serviço externo de mapas estiver indisponível.
			System.err.println("Não foi possível calcular a rota: " + ex.getMessage());
			ex.printStackTrace();
		}
		Viagem viagemSalva = viagemRepository.save(viagem);
		return ResponseEntity.status(HttpStatus.CREATED).body(viagemSalva);
	}

	@PutMapping
	public ResponseEntity<Viagem> put(@Valid @RequestBody Viagem viagem) {
		if (viagem.getId() == null || !viagemRepository.existsById(viagem.getId())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		viagemMapsService.preencherDadosRota(viagem);
		Viagem viagemCalculada = viagemService.calcularEntrega(viagem);

		return ResponseEntity.status(HttpStatus.OK).body(viagemRepository.save(viagemCalculada));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		if (!viagemRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Viagem não encontrada.");
		}
		viagemRepository.deleteById(id);
	}
}