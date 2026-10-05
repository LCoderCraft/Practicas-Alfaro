package com.example.practica06

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practica06.ui.theme.Practica06Theme

/**
 * ListaScreen.kt
 *
 * Pantalla 1: listado con LazyColumn, el equivalente moderno y optimizado
 * del RecyclerView clasico.
 *
 * Solo se renderizan los elementos visibles en pantalla, por eso no saturan
 * la memoria aunque la coleccion tenga miles de registros.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(modifier: Modifier = Modifier) {
    // Contexto local: necesario para el Toast del Reto.
    val context = LocalContext.current

    // Coleccion dinamica de datos.
    val contactos = generarContactos()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Practica 06 - Listas") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            // key evita que Compose reutilice mal un item al reordenar la lista.
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = contactos,
                key = { contacto -> contacto.id }
            ) { contacto ->
                ContactoItem(
                    contacto = contacto,
                    // Reto: mensaje emergente al presionar la tarjeta.
                    onClick = {
                        Toast.makeText(
                            context,
                            "Seleccionaste a ${contacto.nombre}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}

/**
 * Item individual de la lista. Solo dibuja nombre, telefono y correo:
 * las demais variaciones de MaterialTheme ya estan definidas en el tema.
 */
@Composable
fun ContactoItem(
    contacto: Contacto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            // Reto: el modificador clickable sobre la Card completa.
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(contacto.inicial)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = contacto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = contacto.telefonoFormateado,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = contacto.correo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Circulo con la inicial del contacto. */
@Composable
private fun Avatar(inicial: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = inicial,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun ListaScreenPreview() {
    Practica06Theme {
        Surface {
            ListaScreen()
        }
    }
}