package com.generation.carona_api.model;

// Nomes em minúsculo de propósito (foge do estilo Java) para que o JSON
// serialize/deserialize exatamente como "pendente"/"aceita"/"recusada"/
// "cancelada" — os mesmos valores que o front (TypeScript) já espera,
// sem precisar de um conversor Jackson à parte.
public enum StatusReserva {
	pendente,
	aceita,
	recusada,
	cancelada
}
