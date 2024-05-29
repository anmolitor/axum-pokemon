use std::{sync::Arc, time::Duration};
use moka::future::Cache;

use axum::{
    extract::{Path, State},
    response::{IntoResponse, Response},
    routing::get,
    Json, Router,
};
use axum_macros::debug_handler;
use reqwest::Client;
use serde::{Deserialize, Serialize};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    console_subscriber::init();
    let client = reqwest::Client::builder()
        .timeout(Duration::from_secs(2))
        .build()?;
    let cached_client = PokemonCachedClient::new(&client);
    // build our application with a route
    let state = RequestState { cached_client };
    let app = Router::new().route("/:pokemon_name", get(handler)).with_state(state);

    let listener = tokio::net::TcpListener::bind("127.0.0.1:3000")
        .await
        .unwrap();
    println!("listening on {}", listener.local_addr()?);
    axum::serve(listener, app).await?;

    Ok(())
}

#[derive(Clone)]
struct PokemonCachedClient {
    client: reqwest::Client,
    cache: Cache<String, Pokemon>,
}

impl PokemonCachedClient {
    fn new(client: &Client) -> PokemonCachedClient {
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

#[derive(Clone)]
struct RequestState {
    cached_client: PokemonCachedClient,
}

#[debug_handler]
async fn handler(
    State(state): State<RequestState>,
    Path(pokemon_name): Path<String>,
) -> Result<Json<Pokemon>, AppError> {
    let pokemon = state
        .cached_client
        .get_pokemon_by_name(pokemon_name)
        .await?;
    Ok(Json(pokemon))
}

#[derive(Debug)]
enum AppError {
    ClientError(Arc<reqwest::Error>),
}

impl From<Arc<reqwest::Error>> for AppError {
    fn from(value: Arc<reqwest::Error>) -> Self {
        AppError::ClientError(value)
    }
}

impl IntoResponse for AppError {
    fn into_response(self) -> Response {
        http::StatusCode::GATEWAY_TIMEOUT.into_response()
    }
}

#[derive(Debug, Clone, Deserialize, Serialize)]
struct Pokemon {
    types: Vec<WrappedType>,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
struct WrappedType {
    #[serde(rename = "type")]
    type_: Type,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
struct Type {
    name: String,
    url: String,
}
