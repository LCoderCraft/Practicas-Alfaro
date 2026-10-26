package com.example.practica11.presentation

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private enum class EstadoReproductor(val etiqueta: String) {
    SIN_PISTA("Sin pista"),
    DETENIDO("Detenido"),
    REPRODUCIENDO("Reproduciendo"),
    PAUSADO("En pausa")
}

@Composable
fun ReproductorScreen() {
    val context = LocalContext.current

    var reproductor by remember { mutableStateOf<MediaPlayer?>(null) }
    var uriSeleccionada by remember { mutableStateOf<Uri?>(null) }
    var nombrePista by remember { mutableStateOf("") }
    var tamanoPista by remember { mutableStateOf<Long?>(null) }
    var estado by remember { mutableStateOf(EstadoReproductor.SIN_PISTA) }
    var isPlaying by remember { mutableStateOf(false) }
    var duracionMs by remember { mutableLongStateOf(0L) }
    var posicionMs by remember { mutableLongStateOf(0L) }
    var error by remember { mutableStateOf<String?>(null) }

    fun cargarPista(uri: Uri) {
        error = null
        reproductor?.release()
        reproductor = null
        try {
            val nuevo = MediaPlayer.create(context, uri)
            if (nuevo == null) {
                uriSeleccionada = null
                nombrePista = ""
                tamanoPista = null
                duracionMs = 0L
                posicionMs = 0L
                isPlaying = false
                estado = EstadoReproductor.SIN_PISTA
                error = "El archivo no se pudo preparar. Prueba con otro formato."
                return
            }

            // Al terminar la pista se vuelve a colocar en el segundo cero.
            nuevo.setOnCompletionListener { mp ->
                mp.seekTo(0)
                isPlaying = false
                posicionMs = 0L
                estado = EstadoReproductor.DETENIDO
            }
            nuevo.setOnErrorListener { _, que, extra ->
                error = "El reproductor reportó un error ($que/$extra)."
                isPlaying = false
                estado = EstadoReproductor.DETENIDO
                true
            }

            val info = datosDePista(context, uri)
            reproductor = nuevo
            uriSeleccionada = uri
            nombrePista = info.first
            tamanoPista = info.second
            duracionMs = nuevo.duration.toLong().coerceAtLeast(0L)
            posicionMs = 0L
            isPlaying = false
            estado = EstadoReproductor.DETENIDO
        } catch (e: Exception) {
            reproductor = null
            uriSeleccionada = null
            nombrePista = ""
            tamanoPista = null
            duracionMs = 0L
            posicionMs = 0L
            isPlaying = false
            estado = EstadoReproductor.SIN_PISTA
            error = "No se pudo abrir el audio: ${e.message ?: "formato no compatible"}."
        }
    }

    val selectorAudio = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { cargarPista(it) }
    }

    DisposableEffect(Unit) {
        onDispose {
            reproductor?.release()
            reproductor = null
        }
    }

    LaunchedEffect(estado) {
        while (estado == EstadoReproductor.REPRODUCIENDO) {
            reproductor?.let { mp ->
                runCatching { posicionMs = mp.currentPosition.toLong() }
            }
            delay(400L)
        }
    }

    fun alternarReproduccion() {
        val mp = reproductor ?: return
        try {
            if (isPlaying) {
                mp.pause()
                isPlaying = false
                posicionMs = mp.currentPosition.toLong()
                estado = EstadoReproductor.PAUSADO
            } else {
                mp.start()
                isPlaying = true
                estado = EstadoReproductor.REPRODUCIENDO
            }
        } catch (e: Exception) {
            error = "No se pudo controlar la reproducción: ${e.message ?: "error"}."
        }
    }

    // Reto de la práctica: detener no es pausar; además de pausar, la pista
    // regresa al segundo cero con seekTo(0) y isPlaying queda en false.
    fun detenerReproduccion() {
        runCatching {
            reproductor?.pause()
            reproductor?.seekTo(0)
        }
        isPlaying = false
        posicionMs = 0L
        if (reproductor != null) estado = EstadoReproductor.DETENIDO
    }

    val progreso = if (duracionMs > 0L) {
        (posicionMs.toFloat() / duracionMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val detallesPista = buildList {
        add(estado.etiqueta)
        formatearTamano(tamanoPista)?.let { add(it) }
        if (duracionMs > 0L) add(formatearTiempo(duracionMs))
    }.joinToString(" · ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Reproductor MP3",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Selecciona una pista con el explorador de archivos del sistema. " +
                "La app recibe un permiso temporal de lectura mediante SAF.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = { selectorAudio.launch("audio/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.AudioFile,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Seleccionar audio")
        }

        if (uriSeleccionada == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Sin pista seleccionada",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Pulsa «Seleccionar audio» para abrir el explorador. " +
                            "Puedes elegir un archivo de la memoria interna o de la SDCard.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.MusicNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = nombrePista.ifBlank { "Audio seleccionado" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = detallesPista,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatearTiempo(posicionMs),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = formatearTiempo(duracionMs),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = { alternarReproduccion() },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(Modifier.width(28.dp))
                FilledTonalIconButton(
                    onClick = { detenerReproduccion() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "Detener reproducción",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = estado.etiqueta,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Detener pausa la pista, la marca como no reproducida y regresa " +
                    "el audio al segundo cero (00:00).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        error?.let { mensaje ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "No se pudo reproducir",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = mensaje,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Storage Access Framework",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "GetContent() abre el explorador nativo y entrega solo el " +
                            "archivo elegido. No se solicitan permisos globales de audio y " +
                            "el MediaPlayer se libera al salir de esta pantalla.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun datosDePista(context: Context, uri: Uri): Pair<String, Long?> {
    var nombre = uri.lastPathSegment
        ?.substringAfterLast('/')
        ?.takeIf { it.isNotBlank() }
        ?: "Audio seleccionado"
    var tamano: Long? = null

    runCatching {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val indiceNombre = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (indiceNombre >= 0) {
                    nombre = cursor.getString(indiceNombre) ?: nombre
                }
                val indiceTamano = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (indiceTamano >= 0 && !cursor.isNull(indiceTamano)) {
                    tamano = cursor.getLong(indiceTamano)
                }
            }
        }
    }
    return nombre to tamano
}

private fun formatearTiempo(milisegundos: Long): String {
    val totalSegundos = (milisegundos / 1000L).coerceAtLeast(0L)
    val minutos = totalSegundos / 60L
    val segundos = totalSegundos % 60L
    return "%d:%02d".format(minutos, segundos)
}

private fun formatearTamano(bytes: Long?): String? {
    if (bytes == null) return null
    return when {
        bytes >= 1024L * 1024L -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
        bytes >= 1024L -> "%.0f KB".format(bytes / 1024.0)
        else -> "$bytes B"
    }
}
