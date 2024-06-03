package com.example.benchmarks.pokemon.rest;

import java.util.List;

public record MoveDTO(int accuracy,
                      int power,
                      int pp,
                      int priority,
                      List<TypeDTO> types
                      ) {
}
