package com.example.benchmarks.pokemon.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.List;

public record Pokemon(List<Type> types, List<Move> moves, Stats baseStats, int level) {
    @JsonSerialize(using = TypeSerializer.class)
    public record Type(String typeName) {
    }

    @JsonSerialize(using = MoveSerializer.class)
    public record Move(String moveName) {
    }

    public record Stats(int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {
    }

    public Stats getComputedStats() {
        return new Stats(
                computeHpStat(baseStats.hp, 24, 74),
                computeNormalStat(baseStats.attack, 12, 190, 1.1f),
                computeNormalStat(baseStats.defense, 30, 91, 1),
                computeNormalStat(baseStats.specialAttack, 16, 48, 0.9f),
                computeNormalStat(baseStats.specialDefense, 23, 84, 1),
                computeNormalStat(baseStats.speed, 5, 23, 1)
        );
    }

    private int computeHpStat(int hp, int dv, int ev) {
        int computedEv = ev / 4;
        return (2 * hp + dv + computedEv) * level / 100 + level +10;
    }

    private int computeNormalStat(int stat, int dv, int ev, float natureModifier) {
        int computedEv = ev / 4;
        return (int) ( ((2 * stat + dv + computedEv) * level / 100 + 5) * natureModifier);
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
}
