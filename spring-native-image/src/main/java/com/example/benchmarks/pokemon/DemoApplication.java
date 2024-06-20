
package com.example.benchmarks.pokemon;

import com.example.benchmarks.pokemon.domain.Pokemon;
import com.example.benchmarks.pokemon.rest.PokemonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@SpringBootApplication
@RestController
@EnableCaching
public class DemoApplication {

    @Autowired
    public DemoApplication(PokemonApiClient pokemonApiClient) {
        this.pokemonApiClient = pokemonApiClient;
    }

    PokemonApiClient pokemonApiClient;

    private static Map<Integer, Pokemon> savedPokemons = new HashMap<>();

    private static Integer index = 0;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @GetMapping(path = "/{pokemonName}")
    ResponseEntity<Pokemon> pokemon(@PathVariable() String pokemonName) {
        var pokemonDto = this.pokemonApiClient.getPokemonByName(pokemonName);

        String moveName = "razor-wind";

        List<Pokemon.Move> moves = get4RandomMoves(pokemonDto).stream()
                .map(PokemonDTO.MoveIdentifierDTO::name)
                .map(Pokemon.Move::new)
                .toList();

        List<Pokemon.Type> types = pokemonDto.types().stream()
                .map(type -> type.type().name())
                .map(Pokemon.Type::new)
                .toList();

        Map<String, Integer> statMap = pokemonDto.stats().stream()
                .collect(
                        Collectors.toMap(stat -> stat.stat().name(),
                                PokemonDTO.StatDTO::baseStat
                        ));

        Pokemon.Stats stats = new Pokemon.Stats(
                statMap.get("hp"),
                statMap.get("attack"),
                statMap.get("defense"),
                statMap.get("special-attack"),
                statMap.get("special-defense"),
                statMap.get("speed")
        );


        return ResponseEntity.ok(new Pokemon(types, moves, stats, 50));
    }

    public List<PokemonDTO.MoveIdentifierDTO> get4RandomMoves(PokemonDTO pokemon) {
        Collections.shuffle(pokemon.moves());

        return pokemon.moves().subList(0, Math.min(4, pokemon.moves().size())).stream()
                .map(PokemonDTO.WrappedMove::move)
                .toList();
    }
}
