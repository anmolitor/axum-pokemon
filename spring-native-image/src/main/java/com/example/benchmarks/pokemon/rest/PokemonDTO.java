package com.example.benchmarks.pokemon.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PokemonDTO(List<WrappedType> types, List<WrappedMove> moves, List<StatDTO> stats) {
    public record WrappedType(TypeDTO type) {}

    public record TypeDTO(String name) {}

    public record WrappedMove(MoveIdentifierDTO move) {}

    public record MoveIdentifierDTO(String name) {}


    public record StatDTO(@JsonProperty("base_stat") int baseStat,
                          StatValueWrapper stat) {}

    public record StatValueWrapper(String name) {}
}
