use rand::seq::SliceRandom;
use rand::thread_rng;
use serde::Serialize;

use crate::{
    pokemon_api::{NatureDTO, PokemonDTO, WrappedMoveDTO, WrappedTypeDTO},
    stats::{generate_dvs, generate_evs, Stats},
};

#[derive(Debug, Clone, Serialize)]
pub struct Pokemon {
    types: Vec<Type>,
    moves: Vec<Move>,
    stats: Stats<Stat>,
}

#[derive(Debug, Clone, Serialize)]
struct Stat {
    base: u8,
    nature_modifier: f32,
    dv: u8,
    ev: u8,
}

#[derive(Debug, Clone, Serialize)]
struct Type(String);

#[derive(Debug, Clone, Serialize)]
struct Move(String);

impl Pokemon {
    pub fn from_dtos(pokemon_dto: PokemonDTO, nature_dto: NatureDTO) -> Self {
        let moves = pokemon_dto
            .moves
            .choose_multiple(&mut thread_rng(), 4)
            .map(|move_| Move::from(move_.clone()))
            .collect();
        let types = pokemon_dto
            .types
            .into_iter()
            .map(|type_| Type::from(type_.clone()))
            .collect();

        let mut base_stats: Stats<u8> = Default::default();
        for stat in pokemon_dto.stats {
            match stat.stat.name.as_str() {
                "hp" => base_stats.hp = stat.base_stat,
                "attack" => base_stats.attack = stat.base_stat,
                "defense" => base_stats.defense = stat.base_stat,
                "speed" => base_stats.speed = stat.base_stat,
                "special-attack" => base_stats.special_attack = stat.base_stat,
                "special-defense" => base_stats.special_defense = stat.base_stat,
                _ => {}
            }
        }
        let dvs = generate_dvs();
        let evs = generate_evs();
        let nature_stats = Stats::from(nature_dto);

        Pokemon {
            moves,
            types,
            stats: combine_stats(base_stats, nature_stats, dvs, evs),
        }
    }
}

impl From<NatureDTO> for Stats<f32> {
    fn from(value: NatureDTO) -> Self {
        let mut stats: Stats<f32> = Stats {
            hp: 1.0,
            attack: 1.0,
            defense: 1.0,
            special_attack: 1.0,
            special_defense: 1.0,
            speed: 1.0,
        };
        if let Some(stat) = value.increased_stat {
            match stat.name.as_str() {
                "hp" => stats.hp = 1.1,
                "attack" => stats.attack = 1.1,
                "defense" => stats.defense = 1.1,
                "special-attack" => stats.special_attack = 1.1,
                "special-defense" => stats.special_defense = 1.1,
                "speed" => stats.speed = 1.1,
                _ => {}
            }
        }
        if let Some(stat) = value.decreased_stat {
            match stat.name.as_str() {
                "hp" => stats.hp = 0.9,
                "attack" => stats.attack = 0.9,
                "defense" => stats.defense = 0.9,
                "special-attack" => stats.special_attack = 0.9,
                "special-defense" => stats.special_defense = 0.9,
                "speed" => stats.speed = 0.9,
                _ => {}
            }
        }
        stats
    }
}

fn combine_stats(
    base_stats: Stats<u8>,
    nature_modifier: Stats<f32>,
    dvs: Stats<u8>,
    evs: Stats<u8>,
) -> Stats<Stat> {
    Stats {
        hp: Stat {
            base: base_stats.hp,
            nature_modifier: nature_modifier.hp,
            dv: dvs.hp,
            ev: evs.hp,
        },
        attack: Stat {
            base: base_stats.attack,
            nature_modifier: nature_modifier.attack,
            dv: dvs.attack,
            ev: evs.attack,
        },
        defense: Stat {
            base: base_stats.defense,
            nature_modifier: nature_modifier.defense,
            dv: dvs.defense,
            ev: evs.defense,
        },
        special_attack: Stat {
            base: base_stats.special_attack,
            nature_modifier: nature_modifier.special_attack,
            dv: dvs.special_attack,
            ev: evs.special_attack,
        },
        special_defense: Stat {
            base: base_stats.special_defense,
            nature_modifier: nature_modifier.special_defense,
            dv: dvs.special_defense,
            ev: evs.special_defense,
        },
        speed: Stat {
            base: base_stats.speed,
            nature_modifier: nature_modifier.speed,
            dv: dvs.speed,
            ev: evs.speed,
        },
    }
}

#[derive(Debug)]
enum InvalidPokemonError {
    NoTypes,
    MoreThanTwoTypes,
}

impl From<WrappedTypeDTO> for Type {
    fn from(value: WrappedTypeDTO) -> Self {
        Type(value.type_.name)
    }
}

impl From<WrappedMoveDTO> for Move {
    fn from(value: WrappedMoveDTO) -> Self {
        Move(value.move_.name)
    }
}
