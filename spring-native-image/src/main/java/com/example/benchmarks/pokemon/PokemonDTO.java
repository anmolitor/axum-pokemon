package com.example.benchmarks.pokemon;

import java.util.List;

public record PokemonDTO(List<WrappedType> types) {
    public record WrappedType(Type type) {}

    public record Type(String name, String url) {}
}
