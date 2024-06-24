use moka::future::Cache;
use reqwest::Client;
use serde::Deserialize;
use std::sync::Arc;

#[derive(Clone)]
pub struct PokemonCachedClient {
    client: reqwest::Client,
    pokemon_cache: Cache<String, PokemonDTO>,
    nature_cache: Cache<String, NatureDTO>,
    natures_cache: Cache<(), Natures>,
}

impl PokemonCachedClient {
    pub fn new(client: &Client) -> PokemonCachedClient {
        Self {
            client: client.clone(),
            pokemon_cache: Cache::new(100),
            nature_cache: Cache::new(25),
            natures_cache: Cache::new(1),
        }
    }

    pub async fn get_pokemon_by_name(
        &self,
        pokemon_name: String,
    ) -> Result<PokemonDTO, Arc<reqwest::Error>> {
        self.pokemon_cache
            .try_get_with(
                pokemon_name.clone(),
                self.fetch_pokemon_by_name(&pokemon_name),
            )
            .await
    }

    pub async fn get_natures(&self) -> Result<Natures, Arc<reqwest::Error>> {
        self.natures_cache
            .try_get_with((), self.fetch_natures())
            .await
    }

    pub async fn get_nature(&self, nature: String) -> Result<NatureDTO, Arc<reqwest::Error>> {
        self.nature_cache
            .try_get_with(nature.clone(), self.fetch_nature(&nature))
            .await
    }

    async fn fetch_pokemon_by_name(&self, pokemon_name: &str) -> Result<PokemonDTO, reqwest::Error> {
        let response = self
            .client
            .get(format!("https://pokeapi.co/api/v2/pokemon/{pokemon_name}"))
            .send()
            .await?;
        let pokemon = response.json().await?;
        Ok(pokemon)
    }

    async fn fetch_natures(&self) -> Result<Natures, reqwest::Error> {
        self.client
            .get("https://pokeapi.co/api/v2/nature")
            .query(&[("limit", 1000)])
            .send()
            .await?
            .json()
            .await
    }

    async fn fetch_nature(&self, nature: &str) -> Result<NatureDTO, reqwest::Error> {
        self.client
            .get(format!("https://pokeapi.co/api/v2/nature/{nature}"))
            .send()
            .await?
            .json()
            .await
    }
}

#[derive(Debug, Clone, Deserialize)]
pub struct PokemonDTO {
    pub types: Vec<WrappedTypeDTO>,
    pub stats: Vec<StatsDTO>,
    pub moves: Vec<WrappedMoveDTO>,
}

#[derive(Debug, Clone, Deserialize)]
pub struct WrappedTypeDTO {
    #[serde(rename = "type")]
    pub type_: Type,
}

#[derive(Debug, Clone, Deserialize)]
pub struct WrappedMoveDTO {
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
pub struct StatsDTO {
    pub base_stat: u8,
    pub stat: WrappedStatName,
}

#[derive(Debug, Clone, Deserialize)]
pub struct WrappedStatName {
    pub name: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct Natures {
    pub results: Vec<WrappedNature>,
}

#[derive(Debug, Clone, Deserialize)]
pub struct WrappedNature {
    pub name: String,
}

#[derive(Debug, Clone, Deserialize)]
pub struct NatureDTO {
    pub increased_stat: Option<WrappedStatName>,
    pub decreased_stat: Option<WrappedStatName>,
}
