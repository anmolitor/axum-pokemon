package com.example.benchmarks.pokemon.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PokemonTest {

    @Test
    void garchomp() {
        Pokemon garchomp = new Pokemon(
                List.of(),
                List.of(),
                new Stats<>(
                        new Pokemon.Stat(108, 1, 24, 74),
                        new Pokemon.Stat(130, 1.1f, 12, 190),
                        new Pokemon.Stat(95, 1, 30, 91),
                        new Pokemon.Stat(80, 0.9f, 16, 48),
                        new Pokemon.Stat(85, 1, 23, 84),
                        new Pokemon.Stat(102, 1, 5, 23)),
                78
        );

        var computedStats = garchomp.getComputedStats();

        assertEquals(computedStats, new Stats<>(289, 278, 193, 135, 171, 171));
    }
}
