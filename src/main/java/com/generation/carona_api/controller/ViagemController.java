package com.generation.carona_api.controller;

import com.generation.carona_api.model.Viagem;
import com.generation.carona_api.repository.ViagemRepository;
import com.generation.carona_api.service.ViagemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/viagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ViagemController {

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private ViagemService viagemService;

    @GetMapping
    public ResponseEntity<List<Viagem>> getAll() {
        return ResponseEntity.ok(viagemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Viagem> getById(@PathVariable Long id) {
        return viagemRepository.findById(id)
                .map(resposta -> ResponseEntity.ok(resposta))
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

    @PostMapping
    public ResponseEntity<Viagem> post(@Valid @RequestBody Viagem viagem) {
     
        Viagem viagemCalculada = viagemService.calcularEntrega(viagem);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(viagemRepository.save(viagemCalculada));
    }

    @PutMapping
    public ResponseEntity<Viagem> put(@Valid @RequestBody Viagem viagem) {
        if (viagem.getId() == null || !viagemRepository.existsById(viagem.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 
        Viagem viagemCalculada = viagemService.calcularEntrega(viagem);

        return ResponseEntity.status(HttpStatus.OK)
                .body(viagemRepository.save(viagemCalculada));
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