package com.example.practica11.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.practica11.data.FileStorageManager
import com.example.practica11.data.OperacionArchivo
import com.example.practica11.data.UbicacionAlmacenamiento
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ArchivosScreen() {
    val context = LocalContext.current
    val alcance = rememberCoroutineScope()

    var nombre by rememberSaveable { mutableStateOf("nota.txt") }
    var contenido by rememberSaveable { mutableStateOf("") }
    var ubicacion by rememberSaveable { mutableStateOf(UbicacionAlmacenamiento.INTERNA) }
    var operacion by remember { mutableStateOf<OperacionArchivo?>(null) }
    var contenidoLeido by remember { mutableStateOf<String?>(null) }
    var procesando by remember { mutableStateOf(false) }
    var permisosOk by remember { mutableStateOf(FileStorageManager.tienePermisos(context)) }
    var mostrarDialogoPermisos by remember { mutableStateOf(false) }
    var accionPendiente by remember { mutableStateOf(false) }
    var confirmarBorrado by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()

    LaunchedEffect(operacion, contenidoLeido) {
        if (operacion != null) {
            delay(120)
            scroll.animateScrollTo(scroll.maxValue)
        }
    }

    val guardarAhora: () -> Unit = {
        alcance.launch {
            procesando = true
            val resultado = FileStorageManager.guardar(context, ubicacion, nombre, contenido)
            operacion = resultado
            if (resultado.exito) contenidoLeido = null
            procesando = false
        }
    }

    val leerAhora: () -> Unit = {
        alcance.launch {
            procesando = true
            val resultado = FileStorageManager.leer(context, ubicacion, nombre)
            operacion = resultado
            if (resultado.exito) contenidoLeido = resultado.contenido.orEmpty()
            procesando = false
        }
    }

    val borrarAhora: () -> Unit = {
        alcance.launch {
            procesando = true
            val resultado = FileStorageManager.eliminar(context, ubicacion, nombre)
            operacion = resultado
            if (resultado.exito) contenidoLeido = null
            procesando = false
        }
    }

    val lanzadorPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permisosOk = FileStorageManager.tienePermisos(context)
        if (permisosOk) {
            operacion = OperacionArchivo(
                exito = true,
                mensaje = "Permisos concedidos. Ya se puede escribir en la SDCard."
            )
            if (accionPendiente) {
                accionPendiente = false
                guardarAhora()
            }
        } else {
            accionPendiente = false
            operacion = OperacionArchivo(
                exito = false,
                mensaje = "Permiso denegado. La memoria interna sigue disponible."
            )
        }
    }

    fun solicitarGuardado() {
        if (contenido.isBlank()) {
            operacion = OperacionArchivo(false, "Escribe un texto antes de guardar.")
            return
        }
        if (ubicacion == UbicacionAlmacenamiento.SDCARD &&
            FileStorageManager.requierePermisosLegados() &&
            !permisosOk
        ) {
            accionPendiente = true
            mostrarDialogoPermisos = true
        } else {
            guardarAhora()
        }
    }

    val estadoPermisos = FileStorageManager.estadoPermisos(context)
    val rutaDestino = remember(ubicacion, nombre) {
        FileStorageManager.rutaPara(context, ubicacion, nombre)?.absolutePath
            ?: "Almacenamiento no disponible"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Archivos de texto",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Escribe un texto y guárdalo en la memoria interna del teléfono o en la SDCard.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(
            color = if (permisosOk) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (permisosOk) "Almacenamiento listo" else "Permisos pendientes",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = estadoPermisos,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                TextButton(
                    onClick = {
                        if (FileStorageManager.requierePermisosLegados() && !permisosOk) {
                            accionPendiente = false
                            lanzadorPermisos.launch(FileStorageManager.permisosLegados())
                        } else {
                            mostrarDialogoPermisos = true
                        }
                    }
                ) {
                    Text(
                        if (FileStorageManager.requierePermisosLegados() && !permisosOk) {
                            "Solicitar"
                        } else {
                            "Detalles"
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del archivo") },
            supportingText = { Text("Si no escribes extensión se usará .txt") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it },
            label = { Text("Contenido") },
            placeholder = { Text("Escribe aquí el texto que deseas guardar…") },
            minLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Ubicación de destino",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = ubicacion == UbicacionAlmacenamiento.INTERNA,
                onClick = {
                    ubicacion = UbicacionAlmacenamiento.INTERNA
                    operacion = null
                    contenidoLeido = null
                },
                label = { Text("Memoria interna") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Smartphone,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
            FilterChip(
                selected = ubicacion == UbicacionAlmacenamiento.SDCARD,
                onClick = {
                    ubicacion = UbicacionAlmacenamiento.SDCARD
                    operacion = null
                    contenidoLeido = null
                },
                label = { Text("SDCard / externo") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.SdCard,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Ruta del archivo",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = rutaDestino,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { solicitarGuardado() },
                enabled = !procesando,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Guardar")
            }
            FilledTonalButton(
                onClick = { leerAhora() },
                enabled = !procesando,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Leer")
            }
            OutlinedButton(
                onClick = { confirmarBorrado = true },
                enabled = !procesando,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Eliminar")
            }
        }

        if (procesando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        operacion?.let { resultado ->
            TarjetaResultado(resultado)
        }

        contenidoLeido?.let { texto ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Description,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Contenido leído",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    SelectionContainer {
                        Text(
                            text = texto.ifBlank { "(El archivo está vacío)" },
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoPermisos) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoPermisos = false
                accionPendiente = false
            },
            icon = { Icon(Icons.Filled.Security, contentDescription = null) },
            title = { Text("Permisos de almacenamiento") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(estadoPermisos)
                    Text(
                        text = "El AndroidManifest declara:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• READ_EXTERNAL_STORAGE (hasta Android 12L)",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• WRITE_EXTERNAL_STORAGE (hasta Android 9)",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = if (FileStorageManager.requierePermisosLegados()) {
                            "En este dispositivo se solicitarán al guardar en la SDCard " +
                                "para crear el archivo en Documentos/practica11."
                        } else {
                            "Desde Android 10 el sistema aplica Scoped Storage. La app " +
                                "usa su carpeta privada dentro de la SDCard y el selector " +
                                "SAF del reproductor, por lo que no necesita permisos globales."
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                if (FileStorageManager.requierePermisosLegados() && !permisosOk) {
                    TextButton(
                        onClick = {
                            mostrarDialogoPermisos = false
                            lanzadorPermisos.launch(FileStorageManager.permisosLegados())
                        }
                    ) {
                        Text("Solicitar permiso")
                    }
                } else {
                    TextButton(onClick = { mostrarDialogoPermisos = false }) {
                        Text("Entendido")
                    }
                }
            },
            dismissButton = if (FileStorageManager.requierePermisosLegados() && !permisosOk) {
                {
                    TextButton(
                        onClick = {
                            mostrarDialogoPermisos = false
                            accionPendiente = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            } else {
                null
            }
        )
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text("¿Eliminar el archivo?") },
            text = {
                Text(
                    "Se eliminará ${FileStorageManager.nombreSeguro(nombre)} de " +
                        "${ubicacion.etiqueta.lowercase()}."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarBorrado = false
                        borrarAhora()
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TarjetaResultado(resultado: OperacionArchivo) {
    val colorFondo = if (resultado.exito) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val colorTexto = if (resultado.exito) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (resultado.exito) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Filled.ErrorOutline
                    },
                    contentDescription = null,
                    tint = colorTexto,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (resultado.exito) "Operación exitosa" else "No se pudo completar",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorTexto
                )
            }
            Spacer(Modifier.size(6.dp))
            Text(
                text = resultado.mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = colorTexto
            )
            if (resultado.ruta.isNotBlank()) {
                Spacer(Modifier.size(4.dp))
                Text(
                    text = resultado.ruta,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = colorTexto
                )
            }
        }
    }
}
