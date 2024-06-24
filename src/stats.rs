use rand::{Rng, RngCore, SeedableRng};
use serde::Serialize;

#[derive(Debug, Default, Serialize, Clone)]
pub struct Stats<T> {
    pub hp: T,
    pub attack: T,
    pub defense: T,
    pub special_attack: T,
    pub special_defense: T,
    pub speed: T,
}

const MAX_EV: u8 = 252;
const MAX_TOTAL_EVS: u16 = 512;

const MAX_DV: u8 = 31;

pub fn generate_evs() -> Stats<u8> {
    let mut rng = rand::thread_rng();

    let mut total_evs_distributed: u16 = 0;
    let mut stats: [u8; 6] = Default::default();

    while total_evs_distributed < MAX_TOTAL_EVS {
        let stat_index = rng.gen_range(0..6);

        if stats[stat_index] < MAX_EV {
            let evs_to_distribute = rng.gen_range(1..=MAX_EV - stats[stat_index]);
            stats[stat_index] += evs_to_distribute;
            total_evs_distributed += u16::from(evs_to_distribute);
        }
    }

    let [hp, attack, defense, special_attack, special_defense, speed] = stats;

    Stats {
        hp,
        attack,
        defense,
        special_attack,
        special_defense,
        speed,
    }
}

pub fn generate_dvs() -> Stats<u8> {
    let mut rng = rand::thread_rng();

    Stats {
        hp: rng.gen_range(0..=MAX_DV),
        attack: rng.gen_range(0..=MAX_DV),
        defense: rng.gen_range(0..=MAX_DV),
        special_attack: rng.gen_range(0..=MAX_DV),
        special_defense: rng.gen_range(0..=MAX_DV),
        speed: rng.gen_range(0..=MAX_DV),
    }
}
