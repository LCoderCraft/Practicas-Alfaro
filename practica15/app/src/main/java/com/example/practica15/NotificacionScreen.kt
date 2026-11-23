package com.example.practica15

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun NotificacionScreen(
    deepLinkDestino: String?,
    deepLinkMensaje: String?,
    onDeepLinkConsumed: () -> Unit
) {
    val context = LocalContext.current
    var token by remember { mutableStateOf("Obteniendo token...") }
    var permisoConcedido by remember { mutableStateOf(false) }
    var destinoActual by remember { mutableStateOf<String?>(null) }
    var mensajeActual by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permisoConcedido = granted
        if (granted) {
            obtenerToken(context) { token = it }
        }
    }

    LaunchedEffect(Unit) {
        NotificacionesUtils.crearCanal(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permisoConcedido = true
                obtenerToken(context) { token = it }
            } else {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            permisoConcedido = true
            obtenerToken(context) { token = it }
        }
    }

    LaunchedEffect(deepLinkDestino, deepLinkMensaje) {
        if (deepLinkDestino != null) {
            destinoActual = deepLinkDestino
            mensajeActual = deepLinkMensaje
            onDeepLinkConsumed()
        }
    }

    if (destinoActual == "detalle") {
        DetalleScreen(mensaje = mensajeActual) {
            destinoActual = null
            mensajeActual = null
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Practica 15 - Notificaciones Locales y Push", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Permiso de notificaciones: ${if (permisoConcedido) "Concedido" else "Pendiente"}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Token FCM:")
            Text(token, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                NotificacionesUtils.mostrarNotificacionLocal(
                    context,
                    "Notificacion Local",
                    "Hola desde la app",
                    "detalle"
                )
            }) {
                Text("Disparar notificacion local")
            }
        }
    }
}

@Composable
fun DetalleScreen(mensaje: String?, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla de detalle (Deep Link)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(mensaje ?: "Sin mensaje")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

private fun obtenerToken(context: android.content.Context, onResult: (String) -> Unit) {
    if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
        onResult("Configura Firebase: coloca google-services.json en app/")
        return
    }
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            onResult(task.result ?: "Token no disponible")
        } else {
            onResult("Token no disponible: configura google-services.json")
        }
    }
}

