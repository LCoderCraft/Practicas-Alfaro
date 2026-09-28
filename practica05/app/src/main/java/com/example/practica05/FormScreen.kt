package com.example.practica05

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.practica05.data.PreferencesManager
import com.example.practica05.ui.theme.Practica05Theme

/**
 * FormScreen.kt
 *
 * Pantalla de configuracion. Los controles son:
 *  - OutlinedTextField -> nombre del usuario
 *  - Switch           -> notificaciones activas
 *  - Switch           -> modo oscuro
 *  - Botones          -> Guardar / Cargar / Borrar
 *
 * Los valores iniciales se LEEN de SharedPreferences y cada accion
 * vuelve a ESCRIBIR en el mismo archivo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    modifier: Modifier = Modifier,
    onModoOscuroChange: (Boolean) -> Unit = {}
) {
    // LocalContext.current nos da el Context necesario para crear el manager.
    val context = LocalContext.current
    val preferencesManager = remember(context) { PreferencesManager(context) }

    // rememberSaveable: el texto sobrevive a la recreacion de la Activity.
    // Los valores iniciales se LEEN de SharedPreferences, por eso al reabrir
    // la aplicacion los datos guardados aparecen sin pulsar "Cargar".
    var nombre by rememberSaveable {
        mutableStateOf(preferencesManager.getNombre())
    }
    var notificaciones by rememberSaveable {
        mutableStateOf(preferencesManager.areNotificacionesActivas())
    }
    var modoOscuro by rememberSaveable {
        mutableStateOf(preferencesManager.isModoOscuro())
    }
    var estado by rememberSaveable {
        mutableStateOf(
            if (preferencesManager.hayDatosGuardados()) {
                "Datos restaurados desde SharedPreferences"
            } else {
                "Sin datos guardados"
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Practica 05 - SharedPreferences") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Configuracion de la cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // ------------------------------------------------ Campo de texto
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del usuario") },
                placeholder = { Text("Escribe tu nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // ------------------------------------------------------- Switches
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    PreferenceSwitchRow(
                        titulo = "Notificaciones activas",
                        descripcion = "Recibir avisos de la aplicacion",
                        checked = notificaciones,
                        onCheckedChange = { notificaciones = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    PreferenceSwitchRow(
                        titulo = "Modo oscuro",
                        descripcion = "Usa el tema oscuro en la app",
                        checked = modoOscuro,
                        onCheckedChange = {
                            modoOscuro = it
                            onModoOscuroChange(it)
                        }
                    )
                }
            }

            // ------------------------------------------------------- Botones
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        estado = "Error: el nombre no puede estar vacio"
                    } else {
                        preferencesManager.savePreferencias(
                            nombre = nombre.trim(),
                            notificaciones = notificaciones,
                            modoOscuro = modoOscuro
                        )
                        estado = "Guardado: $nombre | notif=$notificaciones | oscuro=$modoOscuro"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (!preferencesManager.hayDatosGuardados()) {
                            estado = "No hay datos guardados todavia"
                        } else {
                            nombre = preferencesManager.getNombre()
                            notificaciones = preferencesManager.areNotificacionesActivas()
                            modoOscuro = preferencesManager.isModoOscuro()
                            onModoOscuroChange(modoOscuro)
                            estado = "Datos leidos de SharedPreferences"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cargar")
                }

                OutlinedButton(
                    onClick = {
                        val ok = preferencesManager.clearPreferencias()
                        nombre = ""
                        notificaciones = false
                        modoOscuro = false
                        onModoOscuroChange(false)
                        estado = if (ok) "Preferencias borradas" else "No se pudo borrar"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Borrar")
                }
            }

            TextButton(
                onClick = {
                    estado = "Archivo interno: data/data/com.example.practica05/shared_prefs/" +
                        "practica05_prefs.xml"
                    Toast.makeText(context, "Ver archivo XML de preferencias", Toast.LENGTH_SHORT)
                        .show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver archivo de preferencias")
            }

            // -------------------------------------------------- Estado actual
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ESTADO",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = estado,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/** Fila reutilizable con titulo, descripcion y un Switch. */
@Composable
private fun PreferenceSwitchRow(
    titulo: String,
    descripcion: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true)
@Composable
fun FormScreenPreview() {
    Practica05Theme {
        FormScreen()
    }
}
