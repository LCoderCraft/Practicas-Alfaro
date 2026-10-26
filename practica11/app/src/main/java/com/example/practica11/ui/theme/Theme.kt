package com.example.practica11.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresClaros = lightColorScheme(
    primary = TealPrincipal,
    onPrimary = Color.White,
    primaryContainer = TealClaro,
    onPrimaryContainer = TealOscuro,
    secondary = AzulSecundario,
    secondaryContainer = AzulContenedor,
    background = Color(0xFFF5FAF9),
    surface = Color(0xFFF5FAF9)
)

private val ColoresOscuros = darkColorScheme(
    primary = TealClaro,
    onPrimary = TealOscuro,
    primaryContainer = TealContenedorOscuro,
    onPrimaryContainer = TealClaro,
    secondary = AzulContenedor
)

@Composable
fun Practica11Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        content = content
    )
}
