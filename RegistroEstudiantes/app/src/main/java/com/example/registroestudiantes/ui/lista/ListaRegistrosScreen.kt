package com.example.registroestudiantes.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.registroestudiantes.data.Estudiante
import com.example.registroestudiantes.data.EstudianteRepository
import com.example.registroestudiantes.ui.theme.RegistroEstudiantesTheme

/**
 * ui/lista/ListaRegistrosScreen.kt
 *
 * Pantalla 3: lista de estudiantes leida desde SharedPreferences.
 *
 * Sirve como evidencia visual de la persistencia: si al reabrir la app
 * aparecen aqui los registros capturados antes, los datos sobrevivieron
 * al cierre del proceso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaRegistrosScreen(
    modifier: Modifier = Modifier,
    onVolver: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = EstudianteRepository(context)
    val estudiantes = repository.obtenerTodos()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Registros guardados (${estudiantes.size})") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (estudiantes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No hay registros guardados",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Registra un estudiante desde el formulario",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(estudiantes) { estudiante ->
                        TarjetaEstudiante(estudiante)
                    }
                }
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Volver al formulario")
            }
        }
    }
}

@Composable
private fun TarjetaEstudiante(estudiante: Estudiante) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (estudiante.activo) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${estudiante.nombre}  (${estudiante.matricula})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Carrera: ${estudiante.carrera}", style = MaterialTheme.typography.bodyMedium)
            Text("Turno: ${estudiante.turno}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Estatus: ${estudiante.estatus}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ListaRegistrosScreenPreview() {
    RegistroEstudiantesTheme {
        ListaRegistrosScreen()
    }
}
