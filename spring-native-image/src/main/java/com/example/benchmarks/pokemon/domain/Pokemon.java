package com.example.benchmarks.pokemon.domain;

import java.util.List;

public record Pokemon (List<Type> types, List<Move> moves) {
    public record Type(String typeName){}
    public record Move(String moveName){}

}
