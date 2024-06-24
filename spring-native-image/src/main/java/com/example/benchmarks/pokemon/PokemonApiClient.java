package com.example.benchmarks.pokemon;

import com.example.benchmarks.pokemon.domain.Pokemon;
import com.example.benchmarks.pokemon.rest.*;
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

    @Cacheable("getNatures")
    public NaturesDTO getNatures() {
        return client.get().uri(builder -> builder.path("/nature").queryParam("limit", 1000).build())
                .retrieve()
                .body(NaturesDTO.class);
    }

    @Cacheable("getNature")
    public NatureDTO getNature(Pokemon.Nature nature) {
        return client.get().uri(builder -> builder.path("/nature/" + nature.natureName()).queryParam("limit", 1000).build())
                .retrieve()
                .body(NatureDTO.class);
    }

    public TypeDTO getTypeInformation(PokemonDTO.TypeDTO typeDTO) {
        return client.get().uri(typeDTO.name()).retrieve().body(TypeDTO.class);
    }

    public MoveDTO getMoveByName(String moveName) {
        return client.get().uri(moveName).retrieve().body(MoveDTO.class);
    }
}
