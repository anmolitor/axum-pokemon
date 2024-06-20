use rand::seq::SliceRandom;
use rand::thread_rng;
use serde::Serialize;

use crate::pokemon_api::{WrappedMove, WrappedType};

#[derive(Debug, Clone, Serialize)]
pub struct Pokemon {
    types: Vec<Type>,
    moves: Vec<Move>,
    hp: u8,
    attack: u8,
    defense: u8,
    special_attack: u8,
    special_defense: u8,
    speed: u8,
}

#[derive(Debug, Clone, Serialize)]
struct Type(String);

#[derive(Debug, Clone, Serialize)]
struct Move(String);

impl From<crate::pokemon_api::Pokemon> for Pokemon {
    fn from(value: crate::pokemon_api::Pokemon) -> Self {
        let moves = value
            .moves
            .choose_multiple(&mut thread_rng(), 4)
            .map(|move_| Move::from(move_.clone()))
            .collect();
        let types = value
            .types
            .into_iter()
            .map(|type_| Type::from(type_.clone()))
            .collect();
        let mut pokemon = Pokemon {
            moves,
            types,
            hp: 0,
            attack: 0,
            special_attack: 0,
            defense: 0,
            special_defense: 0,
            speed: 0,
        };
        for stat in value.stats {
            match stat.stat.name.as_str() {
                "hp" => pokemon.hp = stat.base_stat,
                "attack" => pokemon.attack = stat.base_stat,
                "defense" => pokemon.defense = stat.base_stat,
                "speed" => pokemon.speed = stat.base_stat,
                "special-attack" => pokemon.special_attack = stat.base_stat,
                "special-defense" => pokemon.special_defense = stat.base_stat,
                _ => {}
            }
        }
        pokemon
    }
}

#[derive(Debug)]
enum InvalidPokemonError {
    NoTypes,
    MoreThanTwoTypes,
}

impl From<WrappedType> for Type {
    fn from(value: WrappedType) -> Self {
        Type(value.type_.name)
    }
}

impl From<WrappedMove> for Move {
    fn from(value: WrappedMove) -> Self {
        Move(value.move_.name)
    }
}
