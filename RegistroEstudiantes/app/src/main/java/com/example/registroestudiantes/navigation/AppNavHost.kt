package com.example.registroestudiantes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.registroestudiantes.data.Estudiante
import com.example.registroestudiantes.data.EstudianteRepository
import com.example.registroestudiantes.ui.detalle.DetalleScreen
import com.example.registroestudiantes.ui.lista.ListaRegistrosScreen
import com.example.registroestudiantes.ui.registro.RegistroScreen

/**
 * navigation/AppNavHost.kt
 *
 * Grafo de navegacion de la aplicacion.
 *
 * Los datos NO viajan dentro de la ruta: solo se pasa la matricula como
 * argumento (clave-valor). Al llegar a la pantalla de detalle se reconstruye
 * el objeto Estudiante leyendolo de SharedPreferences con esa matricula.
 * Es la forma recomendada, porque las rutas deben ser cortas y no exponer datos.
 */
object Rutas {
    const val REGISTRO = "registro"
    const val LISTA = "lista"

    // La ruta declara el argumento con {matricula}
    const val DETALLE = "detalle/{matricula}"

    /** Construye la ruta de detalle para una matricula concreta. */
    fun detalle(matricula: String) = "detalle/$matricula"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Rutas.REGISTRO,
        modifier = modifier
    ) {

        // ------------------------------------------------ Pantalla 1: Registro
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistrar = { estudiante ->
                    // navigate() envia el dato a la siguiente pantalla.
                    navController.navigate(Rutas.detalle(estudiante.matricula))
                },
                onVerLista = { navController.navigate(Rutas.LISTA) }
            )
        }

        // ------------------------------------- Pantalla 2: Detalle (argumento)
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(
                navArgument("matricula") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Se recupera el argumento recibido por la navegacion.
            val matricula = backStackEntry.arguments?.getString("matricula").orEmpty()
            val estudiante = EstudianteRepository(context).buscarPorMatricula(matricula)

            if (estudiante != null) {
                DetalleScreen(
                    estudiante = estudiante,
                    totalRegistros = EstudianteRepository(context).obtenerTodos().size,
                    onNuevoRegistro = {
                        navController.popBackStack(Rutas.REGISTRO, inclusive = false)
                    },
                    onVerLista = { navController.navigate(Rutas.LISTA) }
                )
            } else {
                // No se encontro el registro: se vuelve al formulario.
                navController.popBackStack(Rutas.REGISTRO, inclusive = false)
            }
        }

        // ------------------------------------------------ Pantalla 3: Lista
        composable(Rutas.LISTA) {
            ListaRegistrosScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
