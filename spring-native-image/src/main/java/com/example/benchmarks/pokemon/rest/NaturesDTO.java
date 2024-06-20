package com.example.benchmarks.pokemon.rest;

import java.util.List;

public record NaturesDTO(List<NatureWrapper> results) {
    public record NatureWrapper(String name) {
    }
}
