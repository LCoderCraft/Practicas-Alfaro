package com.example.holamundointeractivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.holamundointeractivo.ui.theme.HolaMundoInteractivoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HolaMundoInteractivoTheme {
                PantallaPrincipal()
            }
        }
    }
}

@Composable
fun PantallaPrincipal() {
    // Guarda el texto que escribe el usuario
    var nombre by remember { mutableStateOf("") }
    // Guarda el saludo que se va a mostrar
    var saludo by remember { mutableStateOf("Escribe tu nombre arriba y presiona el boton.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titulo
        Text(
            text = "Hola Mundo Interactivo",
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de texto para capturar el nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Escribe tu nombre") },
            singleLine = true,
            placeholder = { Text("Ej. Maria") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Boton que genera el saludo dinámico
        Button(
            onClick = {
                if (nombre.isNotBlank()) {
                    saludo = "¡Hola, $nombre! Bienvenido al mundo de Android."
                } else {
                    saludo = "Por favor escribe tu nombre primero."
                }
            }
        ) {
            Text("Saludar")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Etiqueta donde se muestra el saludo
        Text(
            text = saludo,
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPrevia() {
    HolaMundoInteractivoTheme {
        PantallaPrincipal()
    }
}