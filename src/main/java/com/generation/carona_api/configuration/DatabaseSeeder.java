package com.generation.carona_api.configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.generation.carona_api.model.Usuario;
import com.generation.carona_api.model.Veiculo;
import com.generation.carona_api.model.Viagem;
import com.generation.carona_api.repository.UsuarioRepository;
import com.generation.carona_api.repository.VeiculoRepository;
import com.generation.carona_api.repository.ViagemRepository;

// Mesmos dados de demonstração que o mock-server.mjs usava — duas
// contas, um veículo cada, seis viagens partindo de "São Paulo, SP"
// (bate com o valor padrão do campo Origem na Home) sempre datadas
// "hoje" (uma busca com a data em branco ou igual a hoje já encontra
// todas). Só roda se o banco estiver vazio.
@Component
public class DatabaseSeeder implements CommandLineRunner {

	// A senha das contas de demonstração do mock era "123456" (6
	// caracteres) — o back real exige no mínimo 8, por isso mudou aqui.
	private static final String SENHA_DEMO = "12345678";

	private final UsuarioRepository usuarioRepository;
	private final VeiculoRepository veiculoRepository;
	private final ViagemRepository viagemRepository;
	private final PasswordEncoder passwordEncoder;

	public DatabaseSeeder(UsuarioRepository usuarioRepository, VeiculoRepository veiculoRepository,
			ViagemRepository viagemRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.veiculoRepository = veiculoRepository;
		this.viagemRepository = viagemRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		if (usuarioRepository.count() > 0) return;

		Usuario demo = criarUsuario("João", "demo@cora.com", "(11) 91234-5678",
				"https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80&fm=webp",
				"feminino", LocalDate.of(1996, 4, 12));

		Usuario carlos = criarUsuario("João", "carlos@cora.com", "(11) 99876-5432",
				"https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80&fm=webp",
				"masculino", LocalDate.of(1990, 9, 23));

		Veiculo veiculoDemo = criarVeiculo(demo, "Nissan Kicks", "ABC1D23", "Prata", 4, true);
		Veiculo veiculoCarlos = criarVeiculo(carlos, "Renault Kwid", "QVR9121", "Vermelho", 4, false);

		criarViagem(demo, veiculoDemo, "São Paulo, SP - Avenida Paulista, 900",
				"São Paulo, SP - Av. Brigadeiro Faria Lima, 2777", "Itaim Bibi", hojeAs(9, 0),
				true, true, true, 3, -23.5613, -46.6565, -23.587, -46.6842);

		criarViagem(carlos, veiculoCarlos, "São Paulo, SP - Rua Palestra Itália, 200",
				"São Paulo, SP - Shopping Aricanduva", "Zona Leste", hojeAs(11, 0),
				false, false, false, 2, -23.5352, -46.6816, -23.5677, -46.5136);

		criarViagem(carlos, veiculoCarlos, "São Paulo, SP - Terminal Tietê",
				"Guarulhos, SP - Centro", "Guarulhos", hojeAs(7, 30),
				true, false, false, 4, -23.5151, -46.6256, -23.4538, -46.5333);

		criarViagem(demo, veiculoDemo, "São Paulo, SP - Estação Pinheiros",
				"Osasco, SP - Centro", "Osasco", hojeAs(14, 30),
				false, false, true, 1, -23.5673, -46.7019, -23.5325, -46.7917);

		criarViagem(demo, veiculoDemo, "São Paulo, SP - Vila Mariana",
				"Santo André, SP - Centro", "ABC Paulista", hojeAs(19, 0),
				false, true, false, 2, -23.5893, -46.6349, -23.6639, -46.5383);

		criarViagem(carlos, veiculoCarlos, "São Paulo, SP - Av. Engenheiro Luís Carlos Berrini",
				"Campinas, SP", "Campinas", hojeAs(6, 45),
				true, false, true, 3, -23.6089, -46.6947, -22.9099, -47.0626);
	}

	private Usuario criarUsuario(String nome, String email, String celular, String foto, String genero,
			LocalDate nascimento) {
		Usuario usuario = new Usuario();
		usuario.setNomeReal(nome);
		usuario.setComoChamar(nome);
		usuario.setUsuario(email);
		usuario.setSenha(passwordEncoder.encode(SENHA_DEMO));
		usuario.setCelular(celular);
		usuario.setFoto(foto);
		usuario.setGenero(genero);
		usuario.setDataNascimento(nascimento);
		return usuarioRepository.save(usuario);
	}

	private Veiculo criarVeiculo(Usuario dono, String modelo, String placa, String cor, int capacidade,
			boolean acessivelPcd) {
		Veiculo veiculo = new Veiculo();
		veiculo.setUsuario(dono);
		veiculo.setModelo(modelo);
		veiculo.setPlaca(placa);
		veiculo.setCor(cor);
		veiculo.setFoto("");
		veiculo.setCapacidade(capacidade);
		veiculo.setAcessivelPcd(acessivelPcd);
		return veiculoRepository.save(veiculo);
	}

	private void criarViagem(Usuario motorista, Veiculo veiculo, String partida, String destino,
			String bairroDestino, LocalDateTime data, boolean disponivelPCD, boolean apenasMulheres,
			boolean aceitaPet, int vagasDisponiveis, double latPartida, double lonPartida, double latDestino,
			double lonDestino) {
		Viagem viagem = new Viagem();
		viagem.setUsuario(motorista);
		viagem.setVeiculo(veiculo);
		viagem.setPartida(partida);
		viagem.setDestino(destino);
		viagem.setBairroDestino(bairroDestino);
		viagem.setData(data);
		viagem.setDisponivelPCD(disponivelPCD);
		viagem.setApenasMulheres(apenasMulheres);
		viagem.setAceitaPet(aceitaPet);
		viagem.setVagasDisponiveis(vagasDisponiveis);
		viagem.setLatitudePartida(latPartida);
		viagem.setLongitudePartida(lonPartida);
		viagem.setLatitudeDestino(latDestino);
		viagem.setLongitudeDestino(lonDestino);

		double distanciaKm = distanciaAproximadaKm(latPartida, lonPartida, latDestino, lonDestino);
		double velocidadeMedia = 40;
		viagem.setDistanciaKm(Math.round(distanciaKm * 100.0) / 100.0);
		viagem.setVelocidadeMedia((int) velocidadeMedia);
		viagem.setTempoEstimadoMin((double) Math.max(5, Math.round(distanciaKm / velocidadeMedia * 60)));
		double valorSugerido = Math.round((4 + distanciaKm * 1.35) * 100.0) / 100.0;
		viagem.setValorSugerido(valorSugerido);
		viagem.setValorTotal(valorSugerido);

		viagemRepository.save(viagem);
	}

	// Fórmula de Haversine — só pra a demonstração ter números plausíveis
	// sem chamar o serviço externo de mapas na inicialização.
	private double distanciaAproximadaKm(double lat1, double lon1, double lat2, double lon2) {
		int raioTerraKm = 6371;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return raioTerraKm * c;
	}

	private LocalDateTime hojeAs(int hora, int minuto) {
		return LocalDateTime.of(LocalDate.now(), LocalTime.of(hora, minuto));
	}
}
