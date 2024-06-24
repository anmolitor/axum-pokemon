package com.example.benchmarks.pokemon.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Scope("singleton")
public class DVGenerator {
    private static final Random random = new Random();

    private static final int MAX_DV = 31;

    public Stats<Integer> generate() {
        return new Stats<>(
                random.nextInt(MAX_DV + 1),
                random.nextInt(MAX_DV + 1),
                random.nextInt(MAX_DV + 1),
                random.nextInt(MAX_DV + 1),
                random.nextInt(MAX_DV + 1),
                random.nextInt(MAX_DV + 1)
        );
    }
}
