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
                new Pokemon.Stats(108, 130, 95, 80, 85, 102),
                78
        );

        Pokemon.Stats computedStats = garchomp.getComputedStats();

        assertEquals(computedStats, new Pokemon.Stats(289, 278, 193, 135, 171, 171));
    }
}
