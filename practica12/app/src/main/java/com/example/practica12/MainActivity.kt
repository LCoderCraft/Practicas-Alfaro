package com.example.practica12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practica12.presentation.PokeViewModel
import com.example.practica12.presentation.PokemonDetailUiState
import com.example.practica12.presentation.PokemonListUiState
import com.example.practica12.presentation.PokemonDetailScreen
import com.example.practica12.presentation.PokemonListScreen
import com.example.practica12.presentation.TypeListScreen
import com.example.practica12.ui.theme.Practica12Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica12Theme {
                PokeApp()
            }
        }
    }
}

@Composable
fun PokeApp(viewModel: PokeViewModel = viewModel()) {
    val navController = rememberNavController()
    val typesState by viewModel.typesState.collectAsStateWithLifecycle()
    val listState by viewModel.listState.collectAsStateWithLifecycle()
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "types"
    ) {
        composable("types") {
            TypeListScreen(
                state = typesState,
                onTypeSelected = { typeName ->
                    navController.navigate("type/$typeName")
                },
                onRetry = { viewModel.loadTypes(force = true) }
            )
        }

        composable(
            route = "type/{typeName}",
            arguments = listOf(navArgument("typeName") { type = NavType.StringType })
        ) { backStackEntry ->
            val typeName = backStackEntry.arguments?.getString("typeName").orEmpty()

            LaunchedEffect(typeName) {
                viewModel.loadPokemonByType(typeName)
            }

            PokemonListScreen(
                typeName = typeName,
                state = if (listState.typeName == typeName) {
                    listState
                } else {
                    PokemonListUiState(typeName = typeName, isLoading = true)
                },
                onBack = { navController.popBackStack() },
                onPokemonSelected = { name ->
                    navController.navigate("pokemon/$name")
                },
                onRetry = { viewModel.loadPokemonByType(typeName, force = true) }
            )
        }

        composable(
            route = "pokemon/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name").orEmpty()

            LaunchedEffect(name) {
                viewModel.loadPokemon(name)
            }

            PokemonDetailScreen(
                state = if (detailState.name == name) {
                    detailState
                } else {
                    PokemonDetailUiState(name = name, isLoading = true)
                },
                onBack = { navController.popBackStack() },
                onRetry = { viewModel.loadPokemon(name, force = true) }
            )
        }
    }
}
