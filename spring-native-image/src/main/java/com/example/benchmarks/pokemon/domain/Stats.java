package com.example.benchmarks.pokemon.domain;

public record Stats<T>(T hp, T attack, T defense, T specialAttack, T specialDefense, T speed) {
}
