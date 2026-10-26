package com.example.practica12.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica12.data.PokeRepository
import com.example.practica12.data.PokemonCard
import com.example.practica12.data.PokemonListItem
import com.example.practica12.data.TypeItem
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class TypesUiState(
    val isLoading: Boolean = false,
    val types: List<TypeItem> = emptyList(),
    val error: String? = null,
    val loaded: Boolean = false
)

data class PokemonListUiState(
    val typeName: String = "",
    val isLoading: Boolean = false,
    val pokemon: List<PokemonListItem> = emptyList(),
    val error: String? = null,
    val loaded: Boolean = false
)

data class PokemonDetailUiState(
    val name: String = "",
    val isLoading: Boolean = false,
    val pokemon: PokemonCard? = null,
    val error: String? = null,
    val loaded: Boolean = false
)

class PokeViewModel(
    private val repository: PokeRepository = PokeRepository()
) : ViewModel() {

    private val _typesState = MutableStateFlow(TypesUiState())
    val typesState: StateFlow<TypesUiState> = _typesState.asStateFlow()

    private val _listState = MutableStateFlow(PokemonListUiState())
    val listState: StateFlow<PokemonListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(PokemonDetailUiState())
    val detailState: StateFlow<PokemonDetailUiState> = _detailState.asStateFlow()

    init {
        loadTypes()
    }

    fun loadTypes(force: Boolean = false) {
        val actual = _typesState.value
        if (!force && (actual.loaded || actual.isLoading)) return

        _typesState.value = actual.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val types = repository.getTypes()
                _typesState.value = TypesUiState(
                    isLoading = false,
                    types = types,
                    loaded = true
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _typesState.value = _typesState.value.copy(
                    isLoading = false,
                    error = mensajeError(e)
                )
            }
        }
    }

    fun loadPokemonByType(typeName: String, force: Boolean = false) {
        val actual = _listState.value
        if (!force && actual.typeName == typeName && (actual.loaded || actual.isLoading)) return

        _listState.value = PokemonListUiState(typeName = typeName, isLoading = true)
        viewModelScope.launch {
            try {
                val collection = repository.getPokemonByType(typeName)
                _listState.value = PokemonListUiState(
                    typeName = collection.typeName,
                    isLoading = false,
                    pokemon = collection.pokemon,
                    loaded = true
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    error = mensajeError(e)
                )
            }
        }
    }

    fun loadPokemon(name: String, force: Boolean = false) {
        val actual = _detailState.value
        if (!force && actual.name == name && (actual.loaded || actual.isLoading)) return

        _detailState.value = PokemonDetailUiState(name = name, isLoading = true)
        viewModelScope.launch {
            try {
                val pokemon = repository.getPokemon(name)
                _detailState.value = PokemonDetailUiState(
                    name = name,
                    isLoading = false,
                    pokemon = pokemon,
                    loaded = true
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detailState.value = _detailState.value.copy(
                    isLoading = false,
                    error = mensajeError(e)
                )
            }
        }
    }

    private fun mensajeError(error: Throwable): String = when (error) {
        is IOException -> "No pudimos conectar con PokeAPI. Revisa tu conexión e inténtalo de nuevo."
        is HttpException -> if (error.code() == 404) {
            "La Pokédex no encontró esa entrada. Vuelve a intentarlo."
        } else {
            "PokeAPI respondió con el código ${error.code()}. Inténtalo de nuevo."
        }
        else -> "Algo salió mal al consultar la Pokédex. Inténtalo otra vez."
    }
}
