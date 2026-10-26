package com.example.practica12.data

import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {

    @GET("type")
    suspend fun getTypes(): TypeListResponse

    @GET("type/{name}")
    suspend fun getType(@Path("name") name: String): TypeDetailResponse

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): PokemonResponse
}
