# Carona_api 🚗

API REST para um sistema de caronas compartilhadas (estilo BlaBlaCar), desenvolvida em **Java com Spring Boot**. 
O projeto permite que usuários cadastrem veículos, criem viagens com rotas calculadas automaticamente e gerenciem informações de caronas de ponta a ponta.

> Projeto Integrador desenvolvido durante o curso de formação em desenvolvimento (Generation).

## ✨ Funcionalidades

- **Cadastro e autenticação de usuários** com login seguro via JWT (Spring Security)
- **Gestão de veículos**, com validação de placa (padrões antigo e Mercosul), modelo, cor e capacidade
- **Criação de viagens (caronas)** informando partida, destino, data/hora, valor por km e velocidade média
- **Integração com API de mapas (OpenStreetMap / OSRM)** para:
  - Geocodificação de endereços (converter endereço em latitude/longitude)
  - Busca e autocomplete de endereços
  - Cálculo automático de distância e tempo estimado de viagem entre partida e destino
- **Documentação interativa** da API via Swagger/OpenAPI

## 🛠️ Tecnologias utilizadas

- Java + Spring Boot
- Spring Security + JWT (autenticação e autorização)
- Spring Data JPA (persistência)
- MySQL / PostgreSQL (produção) e H2 (testes)
- Bean Validation (Jakarta Validation) para regras de negócio
- OpenStreetMap Nominatim (geocodificação) e OSRM (cálculo de rotas)
- Swagger / OpenAPI (documentação dos endpoints)

## 🗂️ Estrutura do projeto

```
com.generation.carona_api
├── configuration   # Configurações gerais (segurança, Swagger, cliente de mapas)
├── controller      # Endpoints REST
├── model           # Entidades JPA (Usuario, Veiculo, Viagem, UsuarioLogin)
├── repository      # Interfaces de acesso a dados (Spring Data JPA)
├── security        # Configurações de autenticação/autorização (JWT)
└── service         # Regras de negócio
```

## 📦 Principais entidades

- **Usuario**: dados de cadastro e login (nome, celular, e-mail, senha, foto) e lista de viagens
- **Veiculo**: modelo, placa, cor, foto e capacidade de passageiros
- **Viagem**: partida, destino, data, distância (km), tempo estimado, valor por km, velocidade média, coordenadas de partida/destino, vínculo com usuário e veículo

## 🚀 Como executar

1. Clone o repositório
2. Configure o `application.properties` com os dados do seu banco (MySQL/PostgreSQL)
3. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Acesse a documentação Swagger em:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

---

Desenvolvido por [Higor](https://github.com/Higormu2) - [Nayara](https://github.com/nayarabastos) - [Edson](https://github.com/dinhovdp) - [Guilherme](https://github.com/guitxc) - 
[João Victor](https://github.com/jvribe) - [Paula](https://github.com/paularegina396-ai) - [Thais](https://github.com/ThaisSantanaa) )
