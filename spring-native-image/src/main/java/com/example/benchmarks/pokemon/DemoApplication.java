
package com.example.benchmarks.pokemon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@EnableCaching
public class DemoApplication {

    @Autowired
    PokemonApiClient pokemonApiClient;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{pokemonName}")
    ResponseEntity<PokemonDTO> pokemon(@PathVariable() String pokemonName) {
        var pokemon = this.pokemonApiClient.getPokemonByName(pokemonName);
        return ResponseEntity.ok(pokemon);
    }
}