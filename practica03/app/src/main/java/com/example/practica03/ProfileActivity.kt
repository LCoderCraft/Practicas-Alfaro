package com.example.practica03

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Activity de destino: recibe el Intent, extrae los extras y muestra la información.
class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Se extraen los parámetros adjuntos por MainScreen mediante putExtra().
        val nombre = intent.getStringExtra("nombre") ?: ""
        val correo = intent.getStringExtra("correo") ?: ""

        setContent {
            ProfileScreen(
                nombre = nombre,
                correo = correo,
                onBackClick = { finish() }
            )
        }
    }
}