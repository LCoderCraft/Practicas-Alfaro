package com.example.practica06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practica06.ui.theme.Practica06Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica06Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    App()
                }
            }
        }
    }
}

/**
 * Permite alternar entre las dos implementaciones del listado:
 * LazyColumn (Compose) y RecyclerView (vistas XML).
 */
@Composable
fun App() {
    // rememberSaveable: la pantalla elegida sobrevive a la recreacion.
    var usarRecyclerView by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedButton(
            onClick = { usarRecyclerView = !usarRecyclerView },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (usarRecyclerView) {
                    "Ver version con LazyColumn"
                } else {
                    "Ver version con RecyclerView"
                },
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (usarRecyclerView) {
            RecyclerScreen()
        } else {
            ListaScreen()
        }
    }
}