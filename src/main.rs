use std::{sync::Arc, time::Duration};

use axum::{
    extract::{Path, State},
    response::{Html, IntoResponse, Response},
    routing::get,
    Json, Router,
};
use serde::Deserialize;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = reqwest::Client::builder()
        .timeout(Duration::from_secs(2))
        .build()?;
    // build our application with a route
    let state = RequestState { client };
    let app: Router<RequestState> = Router::new().route("/", get(handler));
    //.with_state(state);

    let response = client
        .get("https://pokeapi.co/api/v2/pokemon/ditto")
        .send()
        .await?;
    let ditto: Pokemon = response.json().await?;
    println!("{ditto:?}");

    let listener = tokio::net::TcpListener::bind("127.0.0.1:3000")
        .await
        .unwrap();
    println!("listening on {}", listener.local_addr()?);
    axum::serve(listener, app.with_state(state)).await?;

    Ok(())
}

#[derive(Clone)]
struct RequestState {
    client: reqwest::Client,
}

async fn handler(
    State(state): State<RequestState>,
    Path(pokemon_name): Path<String>,
) -> Json<Pokemon> {
    let response = state
        .client
        .get(format!("https://pokeapi.co/api/v2/pokemon/{pokemon_name}"))
        .send()
        .await
        .unwrap();
    let pokemon = response.json().await.unwrap();
    Json(pokemon)
}

#[derive(Debug)]
enum AppError {
    ClientError(reqwest::Error),
}

impl From<reqwest::Error> for AppError {
    fn from(value: reqwest::Error) -> Self {
        AppError::ClientError(value)
    }
}

impl IntoResponse for AppError {
    fn into_response(self) -> Response {
        http::StatusCode::GATEWAY_TIMEOUT.into_response()
    }
}

#[derive(Debug, Clone, Deserialize)]
struct Pokemon {
    types: Vec<WrappedType>,
}

#[derive(Debug, Clone, Deserialize)]
struct WrappedType {
    #[serde(rename = "type")]
    type_: Type,
}

#[derive(Debug, Clone, Deserialize)]
struct Type {
    name: String,
    url: String,
}