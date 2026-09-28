package com.example.registroestudiantes.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.registroestudiantes.data.Estudiante
import com.example.registroestudiantes.data.EstudianteRepository
import com.example.registroestudiantes.data.PreferencesManager
import com.example.registroestudiantes.ui.theme.RegistroEstudiantesTheme

/**
 * ui/registro/RegistroScreen.kt
 *
 * Pantalla 1 del Miniproyecto Integrador: formulario de registro.
 *
 * Controles de captura:
 *  - OutlinedTextField -> Matricula y Nombre completo
 *  - ExposedDropdownMenuBox -> Carrera
 *  - RadioButton       -> Turno (Matutino / Vespertino)
 *  - Switch            -> Estatus (Activo / Inactivo)
 *
 * Persistencia: al reabrir la app se recuperan la ultima matricula y la
 * ultima configuracion seleccionada desde SharedPreferences.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    onRegistrar: (Estudiante) -> Unit = {},
    onVerLista: () -> Unit = {}
) {
    val context = LocalContext.current
    val preferencesManager = remember(context) { PreferencesManager(context) }
    val repository = remember(context) { EstudianteRepository(context) }

    // Estado inicial recuperado de SharedPreferences (ultimo registro).
    val ultimo = remember(context) { preferencesManager.getUltimoEstudiante() }

    var matricula by rememberSaveable { mutableStateOf(ultimo?.matricula ?: "") }
    var nombre by rememberSaveable { mutableStateOf(ultimo?.nombre ?: "") }
    var carrera by rememberSaveable { mutableStateOf(ultimo?.carrera ?: "") }
    var turno by rememberSaveable {
        mutableStateOf(ultimo?.turno ?: Estudiante.TURNO_MATUTINO)
    }
    var activo by rememberSaveable { mutableStateOf(ultimo?.activo ?: true) }
    var error by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Registro de Estudiantes") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Aviso de que los datos se recuperaron de SharedPreferences.
            if (ultimo != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = "Ultimo registro recuperado: ${ultimo.matricula} - ${ultimo.nombre}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Seccion("Datos personales")

            // ------------------------------------------- OutlinedTextField x2
            OutlinedTextField(
                value = matricula,
                onValueChange = { matricula = it; error = "" },
                label = { Text("Matricula") },
                placeholder = { Text("Ej. 20234567") },
                singleLine = true,
                isError = error.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; error = "" },
                label = { Text("Nombre completo") },
                placeholder = { Text("Ej. Juan Perez Lopez") },
                singleLine = true,
                isError = error.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )

            // ------------------------------------------ ExposedDropdownMenu
            Seccion("Carrera")
            SelectorCarrera(
                valor = carrera,
                opciones = Estudiante.CARRERAS,
                onSeleccion = { carrera = it; error = "" }
            )

            // ----------------------------------------------------- RadioButton
            Seccion("Turno")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Estudiante.TURNOS.forEach { opcion ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = turno == opcion,
                            onClick = { turno = opcion }
                        )
                        Text(opcion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // ---------------------------------------------------------- Switch
            Seccion("Estatus")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Estatus", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (activo) Estudiante.ESTATUS_ACTIVO else Estudiante.ESTATUS_INACTIVO,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = activo, onCheckedChange = { activo = it })
                }
            }

            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // ---------------------------------------------------------- Botones
            Button(
                onClick = {
                    val mensaje = validar(matricula, nombre, carrera, turno)
                    if (mensaje != null) {
                        error = mensaje
                    } else {
                        val estudiante = Estudiante(
                            matricula = matricula.trim(),
                            nombre = nombre.trim(),
                            carrera = carrera,
                            turno = turno,
                            estatus = if (activo) Estudiante.ESTATUS_ACTIVO
                            else Estudiante.ESTATUS_INACTIVO
                        )
                        // Persistencia antes de navegar.
                        preferencesManager.guardarUltimoRegistro(estudiante)
                        repository.agregar(estudiante)
                        onRegistrar(estudiante)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar estudiante")
            }

            OutlinedButton(
                onClick = onVerLista,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver registros guardados")
            }

            TextButton(
                onClick = {
                    preferencesManager.limpiar()
                    repository.eliminarTodos()
                    matricula = ""; nombre = ""; carrera = ""
                    turno = Estudiante.TURNO_MATUTINO; activo = true
                    error = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpiar formulario y datos guardados")
            }
        }
    }
}

/**
 * ExposedDropdownMenuBox: menu desplegable para elegir la carrera.
 * menuExpanded controla si la lista esta visible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorCarrera(
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = menuExpanded,
        onExpandedChange = { menuExpanded = it }
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text("Carrera") },
            placeholder = { Text("Selecciona una carrera") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        menuExpanded = false
                    }
                )
            }
        }
    }
}

/** Encabezado de seccion dentro del formulario. */
@Composable
private fun Seccion(titulo: String) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
    }
}

/** Valida los campos obligatorios y devuelve el mensaje de error, o null. */
private fun validar(matricula: String, nombre: String, carrera: String, turno: String): String? = when {
    matricula.isBlank() -> "La matricula es obligatoria"
    matricula.length < 4 -> "La matricula debe tener al menos 4 caracteres"
    nombre.isBlank() -> "El nombre completo es obligatorio"
    carrera.isBlank() -> "Selecciona una carrera"
    turno.isBlank() -> "Selecciona un turno"
    else -> null
}

@androidx.compose.runtime.Composable
fun RegistroScreenPreview() {
    RegistroEstudiantesTheme {
        RegistroScreen()
    }
}
