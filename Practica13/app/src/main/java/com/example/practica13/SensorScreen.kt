package com.example.practica13

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.sqrt

private const val SHAKE_THRESHOLD = 30f
private const val SHAKE_COOLDOWN_MS = 500L
private const val SHAKE_FLASH_MS = 1200L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var axeX by remember { mutableFloatStateOf(0f) }
    var axeY by remember { mutableFloatStateOf(0f) }
    var axeZ by remember { mutableFloatStateOf(0f) }
    var shakeCount by remember { mutableIntStateOf(0) }
    var isShake by remember { mutableStateOf(false) }
    var sensorDisponible by remember { mutableStateOf(true) }
    var lecturas by remember { mutableIntStateOf(0) }

    val magnitud = remember(axeX, axeY, axeZ) {
        sqrt(axeX * axeX + axeY * axeY + axeZ * axeZ)
    }

    val colorFondo = if (isShake) Color(0xFFFFD1D1) else Color(0xFFF3F4F6)
    val colorEstado = if (isShake) Color(0xFFDC2626) else Color(0xFF2563EB)

    LaunchedEffect(isShake) {
        if (isShake) {
            delay(SHAKE_FLASH_MS)
            isShake = false
        }
    }

    val ultimoAviso = remember { mutableStateOf(0L) }

    fun registrarSacudida() {
        val ahora = System.currentTimeMillis()
        if (ahora - ultimoAviso.value < SHAKE_COOLDOWN_MS) return
        ultimoAviso.value = ahora
        shakeCount++
        isShake = true
    }

    DisposableEffect(lifecycleOwner) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorDisponible = acelerometro != null

        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return
                axeX = event.values[0]
                axeY = event.values[1]
                axeZ = event.values[2]
                lecturas++
                val mag = sqrt(axeX * axeX + axeY * axeY + axeZ * axeZ)
                if (mag > SHAKE_THRESHOLD) {
                    val ahora = System.currentTimeMillis()
                    if (ahora - ultimoAviso.value >= SHAKE_COOLDOWN_MS) {
                        ultimoAviso.value = ahora
                        shakeCount++
                        isShake = true
                    }
                }
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (acelerometro != null) {
                        sensorManager.registerListener(
                            listener,
                            acelerometro,
                            SensorManager.SENSOR_DELAY_GAME
                        )
                    }
                }
                Lifecycle.Event.ON_PAUSE -> sensorManager.unregisterListener(listener)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensorManager.unregisterListener(listener)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sensores · Acelerómetro") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colorEstado),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isShake) "¡SACUDIDA DETECTADA!" else "Sin sacudida",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sacudidas: $shakeCount · Umbral: $SHAKE_THRESHOLD m/s²",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaEje(
                    titulo = "Eje X",
                    valor = axeX,
                    color = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f)
                )
                TarjetaEje(
                    titulo = "Eje Y",
                    valor = axeY,
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
                TarjetaEje(
                    titulo = "Eje Z",
                    valor = axeZ,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Magnitud vectorial  √(X²+Y²+Z²)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = formatear(magnitud) + " m/s²",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = if (sensorDisponible) "Sensor activo · $lecturas lecturas" else "Acelerómetro no disponible",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (!sensorDisponible) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }

            Button(
                onClick = {
                    axeX = 18f
                    axeY = -14f
                    axeZ = 22f
                    registrarSacudida()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simular sacudida (emulador)")
            }
            OutlinedButton(
                onClick = { shakeCount = 0 },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reiniciar contador")
            }
            Text(
                text = "Sacude el dispositivo con fuerza para probar la deteccion real.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TarjetaEje(
    titulo: String,
    valor: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = titulo, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatear(valor),
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = "m/s²", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatear(v: Float): String =
    String.format(Locale.US, "%.2f", v)
