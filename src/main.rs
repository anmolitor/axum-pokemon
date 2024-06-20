mod pokemon_api;
mod pokemon;

use std::{sync::Arc, time::Duration};

use axum::{
    extract::{Path, State},
    response::{IntoResponse, Response},
    routing::get,
    Json, Router,
};
use axum_macros::debug_handler;
use crate::pokemon_api::PokemonCachedClient;
use crate::pokemon::Pokemon;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    console_subscriber::init();
    let client = reqwest::Client::builder()
        .timeout(Duration::from_secs(2))
        .build()?;
    let cached_client = PokemonCachedClient::new(&client);

    let state = RequestState { cached_client };
    let app = Router::new().route("/:pokemon_name", get(generate_pokemon)).with_state(state);

    let listener = tokio::net::TcpListener::bind("127.0.0.1:4000")
        .await
        .unwrap();
    println!("listening on {}", listener.local_addr()?);
    axum::serve(listener, app).await?;

    Ok(())
}

#[derive(Clone)]
struct RequestState {
    cached_client: PokemonCachedClient,
}

#[debug_handler]
async fn generate_pokemon(
    State(state): State<RequestState>,
    Path(pokemon_name): Path<String>,
) -> Result<Json<Pokemon>, AppError> {
    let pokemon = state
        .cached_client
        .get_pokemon_by_name(pokemon_name)
        .await?;
    let pokemon = pokemon.into();

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
