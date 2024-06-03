package com.example.benchmarks.pokemon.rest;

import java.util.List;

public record TypeDTO(DamageRelationsDTO damageRelations) {
    private record DamageRelationsDTO(
            List<PokemonDTO.TypeDTO> doubleDamageFrom,
            List<PokemonDTO.TypeDTO> doubleDamageTo,
            List<PokemonDTO.TypeDTO> halfDamageFrom,
            List<PokemonDTO.TypeDTO> halfDamageTo,
            List<PokemonDTO.TypeDTO> noDamageFrom,
            List<PokemonDTO.TypeDTO> noDamageTo) {}
}
