package com.example.registroestudiantes.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.registroestudiantes.data.Estudiante
import com.example.registroestudiantes.ui.theme.RegistroEstudiantesTheme

/**
 * ui/detalle/DetalleScreen.kt
 *
 * Pantalla 2: confirmacion / detalle.
 *
 * Recibe los datos del formulario COMO PARAMETROS DE NAVEGACION y los muestra.
 * No vuelve a leer SharedPreferences: la informacion viaja por la ruta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    estudiante: Estudiante,
    modifier: Modifier = Modifier,
    totalRegistros: Int = 1,
    onNuevoRegistro: () -> Unit = {},
    onVerLista: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Confirmacion de registro") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Registro exitoso",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Los datos se guardaron en SharedPreferences",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Datos recibidos por navegacion",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    DatoFila("Matricula", estudiante.matricula)
                    DatoFila("Nombre completo", estudiante.nombre)
                    DatoFila("Carrera", estudiante.carrera)
                    DatoFila("Turno", estudiante.turno)
                    DatoFila("Estatus", estudiante.estatus)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total de registros guardados: $totalRegistros",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Cierra la aplicacion por completo y vuelve a abrirla: " +
                            "el formulario recuperara esta matricula.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = onNuevoRegistro,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar otro estudiante")
            }

            OutlinedButton(
                onClick = onVerLista,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver todos los registros")
            }
        }
    }
}

/** Fila etiqueta / valor reutilizable. */
@Composable
private fun DatoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DetalleScreenPreview() {
    RegistroEstudiantesTheme {
        DetalleScreen(
            estudiante = Estudiante(
                matricula = "20234567",
                nombre = "Juan Perez Lopez",
                carrera = "Ingenieria en Sistemas",
                turno = Estudiante.TURNO_MATUTINO,
                estatus = Estudiante.ESTATUS_ACTIVO
            )
        )
    }
}
