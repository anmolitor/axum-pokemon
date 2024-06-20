
package com.example.benchmarks.pokemon;

import com.example.benchmarks.pokemon.domain.DVGenerator;
import com.example.benchmarks.pokemon.domain.EVGenerator;
import com.example.benchmarks.pokemon.domain.Pokemon;
import com.example.benchmarks.pokemon.domain.Stats;
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
    public DemoApplication(PokemonApiClient pokemonApiClient, EVGenerator evGenerator, DVGenerator  dvGenerator) {
        this.pokemonApiClient = pokemonApiClient;
        this.evGenerator = evGenerator;
        this.dvGenerator = dvGenerator;
    }

    private final PokemonApiClient pokemonApiClient;
    private final EVGenerator evGenerator;
    private final DVGenerator dvGenerator;

    private static Map<Integer, Pokemon> savedPokemons = new HashMap<>();

    private static Integer index = 0;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @GetMapping(path = "/{pokemonName}")
    ResponseEntity<Pokemon> pokemon(@PathVariable() String pokemonName) {
        var pokemonDto = this.pokemonApiClient.getPokemonByName(pokemonName);
        var natures = this.pokemonApiClient.getNatures().results();
        Collections.shuffle(natures);

        Pokemon.Nature nature = new Pokemon.Nature(natures.get(0).name());
        var natureDto = this.pokemonApiClient.getNature(nature);

        List<Pokemon.Move> moves = get4RandomMoves(pokemonDto).stream()
                .map(PokemonDTO.MoveIdentifierDTO::name)
                .map(Pokemon.Move::new)
                .toList();

        List<Pokemon.Type> types = pokemonDto.types().stream()
                .map(type -> type.type().name())
                .map(Pokemon.Type::new)
                .toList();

        Map<String, Integer> baseStatMap = pokemonDto.stats().stream()
                .collect(
                        Collectors.toMap(stat -> stat.stat().name(),
                                PokemonDTO.StatDTO::baseStat
                        ));
        Map<String, Float> natureStatMap = new HashMap<>();
        natureDto.increasedStat().ifPresent(stat -> natureStatMap.put(stat.name(), 1.1f));
        natureDto.decreasedStat().ifPresent(stat -> natureStatMap.put(stat.name(), 0.9f));

        var evs = evGenerator.generate();
        var dvs = dvGenerator.generate();

        Stats<Pokemon.Stat> stats = new Stats<>(
                new Pokemon.Stat(baseStatMap.get("hp"), Optional.ofNullable(natureStatMap.get("hp")).orElse(1f), evs.hp(), dvs.hp()),
                new Pokemon.Stat(baseStatMap.get("attack"), Optional.ofNullable(natureStatMap.get("attack")).orElse(1f), evs.hp(), dvs.hp()),
                new Pokemon.Stat(baseStatMap.get("defense"), Optional.ofNullable(natureStatMap.get("defense")).orElse(1f), evs.hp(), dvs.hp()),
                new Pokemon.Stat(baseStatMap.get("special-attack"), Optional.ofNullable(natureStatMap.get("special-attack")).orElse(1f), evs.hp(), dvs.hp()),
                new Pokemon.Stat(baseStatMap.get("special-defense"), Optional.ofNullable(natureStatMap.get("special-defense")).orElse(1f), evs.hp(), dvs.hp()),
                new Pokemon.Stat(baseStatMap.get("speed"), Optional.ofNullable(natureStatMap.get("speed")).orElse(1f), evs.hp(), dvs.hp())
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
