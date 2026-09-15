package com.generation.carona_api.controller;

import java.util.Comparator;
import java.util.List;

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

import com.generation.carona_api.dto.SugestaoValorRequest;
import com.generation.carona_api.dto.SugestaoValorResponse;
import com.generation.carona_api.dto.ViagemResponseDTO;
import com.generation.carona_api.dto.ViagemUpdateRequest;
import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.Veiculo;
import com.generation.carona_api.model.Viagem;
import com.generation.carona_api.repository.VeiculoRepository;
import com.generation.carona_api.repository.ViagemRepository;
import com.generation.carona_api.security.AutenticacaoAtual;
import com.generation.carona_api.service.ViagemMapsService;
import com.generation.carona_api.service.ViagemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/viagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ViagemController {

	private final ViagemRepository viagemRepository;
	private final ViagemService viagemService;
	private final ViagemMapsService viagemMapsService;
	private final VeiculoRepository veiculoRepository;
	private final AutenticacaoAtual autenticacaoAtual;

	public ViagemController(ViagemRepository viagemRepository, ViagemService viagemService,
			ViagemMapsService viagemMapsService, VeiculoRepository veiculoRepository,
			AutenticacaoAtual autenticacaoAtual) {
		this.viagemRepository = viagemRepository;
		this.viagemService = viagemService;
		this.viagemMapsService = viagemMapsService;
		this.veiculoRepository = veiculoRepository;
		this.autenticacaoAtual = autenticacaoAtual;
	}

	private ViagemResponseDTO paraDTO(Viagem viagem) {
		return ViagemResponseDTO.de(viagem, viagemService.calcularVagasRestantes(viagem));
	}

	@GetMapping
	public ResponseEntity<List<ViagemResponseDTO>> getAll() {
		List<ViagemResponseDTO> todas = viagemRepository.findAll().stream()
				.sorted(Comparator.comparing(Viagem::getData))
				.map(this::paraDTO)
				.toList();
		return ResponseEntity.ok(todas);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ViagemResponseDTO> getById(@PathVariable Long id) {
		Viagem viagem = viagemRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carona não encontrada."));
		return ResponseEntity.ok(paraDTO(viagem));
	}

	@PostMapping("/sugestao-valor")
	public ResponseEntity<SugestaoValorResponse> sugerirValor(@Valid @RequestBody SugestaoValorRequest request) {
		return ResponseEntity.ok(viagemService.calcularSugestaoValor(request));
	}

	@PostMapping
	public ResponseEntity<ViagemResponseDTO> cadastrar(@Valid @RequestBody Viagem viagem) {
		Usuario autenticado = autenticacaoAtual.obter();

		if (viagem.getVeiculo() == null || viagem.getVeiculo().getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id do veículo");
		}
		Veiculo veiculoCompleto = veiculoRepository.findById(viagem.getVeiculo().getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veículo não encontrado"));

		// Carona exclusiva para mulher só vale se quem está dirigindo
		// também se identifica como mulher (mesma regra do mock).
		viagemService.validarCriacaoApenasMulheres(viagem, autenticado);
		viagemService.validarCriacaoPCD(viagem, veiculoCompleto);

		viagem.setId(null);
		viagem.setUsuario(autenticado);
		viagem.setVeiculo(veiculoCompleto);

		try {
			viagemMapsService.preencherDadosRota(viagem);
		} catch (RuntimeException ex) {
			// O cadastro não deve falhar quando o serviço externo de mapas
			// estiver indisponível — a viagem é salva sem rota calculada.
			System.err.println("Não foi possível calcular a rota: " + ex.getMessage());
		}

		Viagem viagemSalva = viagemRepository.save(viagem);
		return ResponseEntity.status(HttpStatus.CREATED).body(paraDTO(viagemSalva));
	}

	// Atualização PARCIAL: só os campos enviados mudam. O front
	// (ResultadosCaronas.tsx) manda só {id, partida, destino, data,
	// disponivelPCD, apenasMulheres, aceitaPet, valorTotal,
	// usuario:{id}, veiculo:{id}} — um bind direto em Viagem zeraria tudo
	// o mais (distância, coordenadas, vagas...). valorSugerido (a
	// referência de preço justo calculada pela rota) só é recalculada
	// via /viagens/sugestao-valor e não é tocada por essa edição.
	@PutMapping
	public ResponseEntity<ViagemResponseDTO> atualizar(@RequestBody ViagemUpdateRequest dados) {
		if (dados.id() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Carona não encontrada.");
		}

		Usuario autenticado = autenticacaoAtual.obter();

		Viagem alvo = viagemRepository.findById(dados.id())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carona não encontrada."));

		if (!alvo.getUsuario().getId().equals(autenticado.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode editar esta carona.");
		}

		String partidaAnterior = alvo.getPartida();
		String destinoAnterior = alvo.getDestino();

		if (dados.partida() != null) alvo.setPartida(dados.partida());
		if (dados.destino() != null) alvo.setDestino(dados.destino());
		if (dados.bairroDestino() != null) alvo.setBairroDestino(dados.bairroDestino());
		if (dados.data() != null) alvo.setData(dados.data());
		if (dados.disponivelPCD() != null) alvo.setDisponivelPCD(dados.disponivelPCD());
		if (dados.apenasMulheres() != null) alvo.setApenasMulheres(dados.apenasMulheres());
		if (dados.aceitaPet() != null) alvo.setAceitaPet(dados.aceitaPet());
		if (dados.valorSugerido() != null) alvo.setValorSugerido(dados.valorSugerido());
		if (dados.valorTotal() != null) alvo.setValorTotal(dados.valorTotal());
		if (dados.vagasDisponiveis() != null) alvo.setVagasDisponiveis(dados.vagasDisponiveis());
		if (dados.veiculo() != null && dados.veiculo().id() != null) {
			Veiculo veiculo = veiculoRepository.findById(dados.veiculo().id())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veículo não encontrado"));
			alvo.setVeiculo(veiculo);
		}

		// Mesma regra da criação: só motorista do gênero feminino pode
		// manter/marcar uma viagem como exclusiva para mulheres.
		viagemService.validarCriacaoApenasMulheres(alvo, autenticado);

		boolean enderecoMudou = !alvo.getPartida().equals(partidaAnterior) || !alvo.getDestino().equals(destinoAnterior);
		if (enderecoMudou) {
			try {
				viagemMapsService.preencherDadosRota(alvo);
			} catch (RuntimeException ex) {
				System.err.println("Não foi possível recalcular a rota: " + ex.getMessage());
			}
		}

		return ResponseEntity.ok(paraDTO(viagemRepository.save(alvo)));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable Long id) {
		Usuario autenticado = autenticacaoAtual.obter();
		Viagem alvo = viagemRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carona não encontrada."));

		if (!alvo.getUsuario().getId().equals(autenticado.getId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode excluir esta carona.");
		}

		viagemRepository.deleteById(id);
	}
}
