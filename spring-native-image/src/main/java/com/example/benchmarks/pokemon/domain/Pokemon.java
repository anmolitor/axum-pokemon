package com.example.benchmarks.pokemon.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.List;

public record Pokemon(List<Type> types, List<Move> moves, Stats<Stat> baseStats, int level) {
    @JsonSerialize(using = TypeSerializer.class)
    public record Type(String typeName) {
    }

    @JsonSerialize(using = MoveSerializer.class)
    public record Move(String moveName) {
    }

    @JsonSerialize(using = NatureSerializer.class)
    public record Nature(String natureName) {
    }


    public record Stat(int base, float natureModifier, int dv, int ev) {
    }

    public Stats<Integer> getComputedStats() {
        return new Stats<>(
                computeHpStat(baseStats.hp()),
                computeNormalStat(baseStats.attack()),
                computeNormalStat(baseStats.defense()),
                computeNormalStat(baseStats.specialAttack()),
                computeNormalStat(baseStats.specialDefense()),
                computeNormalStat(baseStats.speed())
        );
    }

    private int computeHpStat(Stat hp) {
        int computedEv = hp.ev / 4;
        return (2 * hp.base + hp.dv + computedEv) * level / 100 + level + 10;
    }

    private int computeNormalStat(Stat stat) {
        int computedEv = stat.ev / 4;
        return (int) (((2 * stat.base + stat.dv + computedEv) * level / 100 + 5) * stat.natureModifier);
    }


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

    public static class NatureSerializer extends JsonSerializer<Nature> {

        @Override
        public void serialize(Nature nature, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(nature.natureName);
        }
    }
}
