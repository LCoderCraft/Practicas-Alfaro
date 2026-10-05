package com.example.practica06

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practica06.ui.ContactoAdapter
import com.example.practica06.ui.theme.Practica06Theme

/**
 * RecyclerScreen.kt
 *
 * Pantalla 2: el mismo listado construido con el RecyclerView clasico
 * (Adapter + ViewHolder + layout XML), alojado dentro de Compose mediante
 * AndroidView.
 *
 * Aqui se configura el LayoutManager, que define como se acomodan los items:
 *  - LinearLayoutManager  -> una columna (comportamiento estandar)
 *  - GridLayoutManager    -> una cuadricula de 2 columnas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val contactos = generarContactos()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Practica 06 - RecyclerView") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "LinearLayoutManager + Adapter + ViewHolder",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                // La instancia se crea una sola vez al entrar a la pantalla.
                factory = { ctx ->
                    RecyclerView(ctx).apply {
                        // LayoutManager: define la disposicion de los items.
                        layoutManager = LinearLayoutManager(ctx)

                        // El Adapter vincula la coleccion con el RecyclerView.
                        adapter = ContactoAdapter(contactos) { contacto ->
                            Toast.makeText(
                                context,
                                "Seleccionaste a ${contacto.nombre}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        //Mejora del rendimiento durante el scroll.
                        setHasFixedSize(true)
                    }
                }
            )
        }
    }
}

/**
 * Variante en cuadrícula. Se usa GridLayoutManager para demostrar que el
 * mismo Adapter funciona cambiando solo el LayoutManager.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclerGridScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val contactos = generarContactos()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("RecyclerView - GridLayoutManager") }) }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { ctx ->
                RecyclerView(ctx).apply {
                    // 2 columnas en disposicion de cuadricula.
                    layoutManager = GridLayoutManager(ctx, 2)
                    adapter = ContactoAdapter(contactos) { contacto ->
                        Toast.makeText(
                            context,
                            "Seleccionaste a ${contacto.nombre}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    setHasFixedSize(true)
                }
            }
        )
    }
}

@Composable
fun RecyclerScreenPreview() {
    Practica06Theme {
        RecyclerScreen()
    }
}