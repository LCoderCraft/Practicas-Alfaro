package com.example.practica12.data

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PokeRepository(
    private val api: PokeApi = createApi()
) {

    private val typeCache = mutableMapOf<String, TypeDetailResponse>()
    private val pokemonCache = mutableMapOf<String, PokemonCard>()

    suspend fun getTypes(): List<TypeItem> = withContext(Dispatchers.IO) {
        Log.d("PokeApi", "getTypes: inicio")
        val response = api.getTypes()
        Log.d("PokeApi", "getTypes: respuesta recibida count=${response.results.size}")
        response.results.mapNotNull { resource ->
            val id = idFromUrl(resource.url) ?: return@mapNotNull null
            TypeItem(id = id, name = resource.name)
        }.sortedBy { it.id }
    }

    suspend fun getPokemonByType(typeName: String): TypeCollection = withContext(Dispatchers.IO) {
        Log.d("PokeApi", "getPokemonByType: inicio $typeName")
        val detail = typeCache[typeName] ?: api.getType(typeName).also {
            typeCache[typeName] = it
        }
        Log.d("PokeApi", "getPokemonByType: detail=${detail.name} count=${detail.pokemon.size}")
        val pokemon = detail.pokemon.mapNotNull { entry ->
            val id = idFromUrl(entry.pokemon.url) ?: return@mapNotNull null
            PokemonListItem(
                id = id,
                name = entry.pokemon.name,
                imageUrl = artworkUrl(id)
            )
        }.sortedBy { it.id }

        TypeCollection(typeName = detail.name, pokemon = pokemon)
    }

    suspend fun getPokemon(name: String): PokemonCard = withContext(Dispatchers.IO) {
        Log.d("PokeApi", "getPokemon: inicio $name")
        val pokemon = pokemonCache[name] ?: api.getPokemon(name).toCard().also {
            pokemonCache[name] = it
        }
        Log.d("PokeApi", "getPokemon: respuesta recibida id=${pokemon.id}")
        pokemon
    }

    private fun PokemonResponse.toCard(): PokemonCard {
        val baseStats = stats.associate { it.stat.name to it.baseStat }
        return PokemonCard(
            id = id,
            name = name,
            imageUrl = sprites?.other?.officialArtwork?.frontDefault ?: artworkUrl(id),
            types = types.sortedBy { it.slot }.map { it.type.name },
            hp = baseStats["hp"] ?: 0,
            attack = baseStats["attack"] ?: 0,
            defense = baseStats["defense"] ?: 0,
            specialAttack = baseStats["special-attack"] ?: 0,
            specialDefense = baseStats["special-defense"] ?: 0,
            speed = baseStats["speed"] ?: 0,
            heightMeters = height / 10.0,
            weightKg = weight / 10.0,
            abilities = abilities.map { entry ->
                val label = displayName(entry.ability.name)
                if (entry.isHidden) "$label (oculta)" else label
            }.take(4)
        )
    }

    private fun displayName(raw: String): String = raw
        .split("-")
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

    companion object {
        private const val BASE_URL = "https://pokeapi.co/api/v2/"

    private fun createApi(): PokeApi {
        val gson = GsonBuilder().create()
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PokeApi::class.java)
    }

        fun artworkUrl(id: Int): String =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

        private fun idFromUrl(url: String): Int? =
            url.trimEnd('/').substringAfterLast('/').toIntOrNull()
    }
}
