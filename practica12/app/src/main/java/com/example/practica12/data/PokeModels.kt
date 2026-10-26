package com.example.practica12.data

import com.google.gson.annotations.SerializedName

/*
 * Modelos que replican la estructura JSON de PokeAPI.
 * Gson ignora los campos que no necesitamos y llena estos objetos.
 */

data class NamedResource(
    val name: String,
    val url: String
)

data class TypeListResponse(
    val count: Int,
    val results: List<NamedResource>
)

data class TypeDetailResponse(
    val id: Int,
    val name: String,
    val pokemon: List<TypePokemonEntry>
)

data class TypePokemonEntry(
    val slot: Int,
    val pokemon: NamedResource
)

data class PokemonResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeEntry>,
    val abilities: List<PokemonAbilityEntry>,
    val stats: List<PokemonStatEntry>,
    val sprites: Sprites?
)

data class PokemonTypeEntry(
    val slot: Int,
    val type: NamedResource
)

data class PokemonAbilityEntry(
    val ability: NamedResource,
    @SerializedName("is_hidden") val isHidden: Boolean
)

data class PokemonStatEntry(
    @SerializedName("base_stat") val baseStat: Int,
    val effort: Int,
    val stat: NamedResource
)

data class Sprites(
    val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String?
)

/*
 * Modelos ya listos para la interfaz. El repositorio transforma
 * las respuestas de la API en estos objetos más pequeños.
 */

data class TypeItem(
    val id: Int,
    val name: String
)

data class PokemonListItem(
    val id: Int,
    val name: String,
    val imageUrl: String
)

data class TypeCollection(
    val typeName: String,
    val pokemon: List<PokemonListItem>
)

data class PokemonCard(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val types: List<String>,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val specialAttack: Int,
    val specialDefense: Int,
    val speed: Int,
    val heightMeters: Double,
    val weightKg: Double,
    val abilities: List<String>
)
