package com.example.benchmarks.pokemon.rest;

import java.util.List;

public record PokemonDTO(List<WrappedType> types, List<WrappedMove> moves) {
    public record WrappedType(TypeDTO type) {}

    public record TypeDTO(String name) {}

    public record WrappedMove(MoveIdentifierDTO move) {}

    public record MoveIdentifierDTO(String name) {}
}
