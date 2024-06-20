use moka::future::Cache;
use reqwest::Client;
use serde::Deserialize;
use std::sync::Arc;

#[derive(Clone)]
pub struct PokemonCachedClient {
    client: reqwest::Client,
    cache: Cache<String, Pokemon>,
}

impl PokemonCachedClient {
    pub fn new(client: &Client) -> PokemonCachedClient {
        Self {
            client: client.clone(),
            cache: Cache::new(100),
        }
    }

    pub async fn get_pokemon_by_name(
        &self,
        pokemon_name: String,
    ) -> Result<Pokemon, Arc<reqwest::Error>> {
        self.cache
            .try_get_with(
                pokemon_name.clone(),
                self.fetch_pokemon_by_name(&pokemon_name),
            )
            .await
    }

    async fn fetch_pokemon_by_name(&self, pokemon_name: &str) -> Result<Pokemon, reqwest::Error> {
        let response = self
            .client
            .get(format!("https://pokeapi.co/api/v2/pokemon/{pokemon_name}"))
            .send()
            .await?;
        let pokemon = response.json().await?;
        Ok(pokemon)
    }
}

#[derive(Debug, Clone, Deserialize)]
pub struct Pokemon {
    pub types: Vec<WrappedType>,
    pub stats: Vec<Stats>,
    pub moves: Vec<WrappedMove>,
}

#[derive(Debug, Clone, Deserialize)]
pub struct WrappedType {
    #[serde(rename = "type")]
    pub type_: Type,
}

#[derive(Debug, Clone, Deserialize)]
pub struct WrappedMove {
    #[serde(rename = "move")]
    pub move_: Move,
}

#[derive(Debug, Clone, Deserialize)]
pub struct Type {
    pub name: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct Move {
    pub name: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct Stats {
    pub base_stat: u8,
    pub stat: WrappedStatName
}

#[derive(Debug, Clone, Deserialize)]
pub struct WrappedStatName {
    pub name: String
}

