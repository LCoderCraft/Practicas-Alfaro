package com.example.practica09.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

private val imagenes = listOf(
    "https://picsum.photos/seed/galeria01/600/600",
    "https://picsum.photos/seed/galeria02/600/600",
    "https://picsum.photos/seed/galeria03/600/600",
    "https://picsum.photos/seed/galeria04/600/600",
    "https://picsum.photos/seed/galeria05/600/600",
    "https://picsum.photos/seed/galeria06/600/600",
    "https://picsum.photos/seed/galeria07/600/600",
    "https://picsum.photos/seed/galeria08/600/600",
    "https://picsum.photos/seed/galeria09/600/600",
    "https://picsum.photos/seed/galeria10/600/600",
    "https://picsum.photos/seed/galeria11/600/600",
    "https://picsum.photos/seed/galeria12/600/600"
)

@Composable
fun GalleryScreen() {
    var seleccionada by remember { mutableStateOf<String?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 110.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Galería de imágenes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(imagenes, key = { it }) { url ->
            AsyncImage(
                model = url,
                contentDescription = "Imagen de la galería",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { seleccionada = url },
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color(0xFFE5E7EB)),
                error = ColorPainter(Color(0xFFFECACA))
            )
        }
    }

    seleccionada?.let { url ->
        Dialog(onDismissRequest = { seleccionada = null }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = url,
                        contentDescription = "Imagen ampliada",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit,
                        placeholder = ColorPainter(Color(0xFFE5E7EB)),
                        error = ColorPainter(Color(0xFFFECACA))
                    )
                }
            }
        }
    }
}
