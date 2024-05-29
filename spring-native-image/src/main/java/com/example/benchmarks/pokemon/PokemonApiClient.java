package com.example.benchmarks.pokemon;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;

@Service
@Scope("singleton")
public class PokemonApiClient {
    @Cacheable("getPokemonByName")
    public PokemonDTO getPokemonByName(String pokemonName) {
        var client = RestClient.builder().baseUrl("https://pokeapi.co/api/v2").build();
        return client.get().uri("/pokemon/{pokemonName}", pokemonName).retrieve().body(PokemonDTO.class);
    }
}