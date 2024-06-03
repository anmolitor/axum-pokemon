package com.example.benchmarks.pokemon;

import com.example.benchmarks.pokemon.rest.MoveDTO;
import com.example.benchmarks.pokemon.rest.PokemonDTO;
import com.example.benchmarks.pokemon.rest.TypeDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClient;

@Service
@Scope("singleton")
public class PokemonApiClient {

    private final RestClient client;

    public PokemonApiClient() {
        this.client = RestClient.builder().baseUrl("https://pokeapi.co/api/v2").build();
    }

    @Cacheable("getPokemonByName")
    public PokemonDTO getPokemonByName(String pokemonName) {
        return client.get().uri("/pokemon/{pokemonName}", pokemonName).retrieve().body(PokemonDTO.class);
    }

    public TypeDTO getTypeInformation(PokemonDTO.TypeDTO typeDTO) {
        return client.get().uri(typeDTO.name()).retrieve().body(TypeDTO.class);
    }

    public MoveDTO getMoveByName(String moveName) {
        return client.get().uri(moveName).retrieve().body(MoveDTO.class);
    }
}
