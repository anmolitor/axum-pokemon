package com.example.benchmarks.pokemon.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.List;

public record Pokemon (List<Type> types, List<Move> moves, Stats stats) {
    @JsonSerialize(using = TypeSerializer.class)
    public record Type(String typeName){}
    @JsonSerialize(using = MoveSerializer.class)
    public record Move(String moveName){}

    public record Stats(int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {}

    public static class TypeSerializer extends JsonSerializer<Type> {

        @Override
        public void serialize(Type type, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(type.typeName);
        }
    }

    public static class MoveSerializer extends JsonSerializer<Move> {

        @Override
        public void serialize(Move move, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(move.moveName);
        }
    }

}
