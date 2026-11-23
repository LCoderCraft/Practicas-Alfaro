package com.example.practica15

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.practica15.ui.theme.Practica15Theme

class MainActivity : ComponentActivity() {
    private var deepLinkDestino by mutableStateOf<String?>(null)
    private var deepLinkMensaje by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        enableEdgeToEdge()
        setContent {
            Practica15Theme {
                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    NotificacionScreen(
                        deepLinkDestino = deepLinkDestino,
                        deepLinkMensaje = deepLinkMensaje,
                        onDeepLinkConsumed = {
                            deepLinkDestino = null
                            deepLinkMensaje = null
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        deepLinkDestino = intent?.getStringExtra("destino")
        deepLinkMensaje = intent?.getStringExtra("mensaje")
    }
}
