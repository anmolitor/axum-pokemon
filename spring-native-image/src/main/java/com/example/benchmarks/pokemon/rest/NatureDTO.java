package com.example.benchmarks.pokemon.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record NatureDTO(@JsonProperty("increased_stat") Optional<PokemonDTO.StatValueWrapper> increasedStat,
                        @JsonProperty("decreased_stat") Optional<PokemonDTO.StatValueWrapper> decreasedStat) {
}
