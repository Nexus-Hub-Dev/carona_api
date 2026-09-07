package com.generation.carona_api.configuration;

import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.Veiculo;
import com.generation.carona_api.repository.UsuarioRepository;
import com.generation.carona_api.repository.VeiculoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UsuarioRepository usuarioRepository,
                          VeiculoRepository veiculoRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.veiculoRepository = veiculoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        ensureUsuariosBase();
        ensureVeiculosBase();
    }

    private void ensureUsuariosBase() {
        long total = usuarioRepository.count();
        for (long i = total; i < 2; i++) {
            Usuario usuario = new Usuario();
            usuario.setNome(i == 0 ? "Usuario Demo 1" : "Usuario Demo 2");
            usuario.setCelular(i == 0 ? "11999990001" : "11999990002");
            usuario.setUsuario(i == 0 ? "demo1@carona.com" : "demo2@carona.com");
            usuario.setSenha(passwordEncoder.encode("12345678"));
            usuario.setFoto("https://placehold.co/600x400");
            usuario.setGenero("Outro");
            usuarioRepository.save(usuario);
        }
    }

    private void ensureVeiculosBase() {
        long total = veiculoRepository.count();
        for (long i = total; i < 2; i++) {
            Veiculo veiculo = new Veiculo();
            veiculo.setModelo(i == 0 ? "Hyundai HB20" : "Toyota Corolla");
            veiculo.setPlaca(i == 0 ? "CAR1001" : "CAR1002");
            veiculo.setFoto("https://placehold.co/600x400");
            veiculo.setCor(i == 0 ? "Prata" : "Branco");
            veiculo.setCapacidade(4);
            veiculo.setAcessivelPcd(false);
            veiculoRepository.save(veiculo);
        }
    }
}