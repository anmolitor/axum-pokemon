use std::sync::Arc;
use moka::future::Cache;
use reqwest::Client;
use serde::{Deserialize, Serialize};

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

    pub async fn get_pokemon_by_name(&self, pokemon_name: String) -> Result<Pokemon, Arc<reqwest::Error>> {
        self.cache.try_get_with(pokemon_name.clone(), self.fetch_pokemon_by_name(&pokemon_name)).await
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


#[derive(Debug, Clone, Deserialize, Serialize)]
pub struct Pokemon {
    pub types: Vec<WrappedType>,
    pub stats: Vec<Stats>,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
pub struct WrappedType {
    #[serde(rename = "type")]
    pub type_: Type,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
pub struct Type {
    name: String,
    url: String,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
pub struct Stats {
    base_stat: u8,
}