package com.example.practica10.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColoresPokemon = lightColorScheme(
    primary = RojoPokemon,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = RojoSuave,
    onPrimaryContainer = RojoOscuro,
    secondary = AzulPokemon,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFD7E6FA),
    onSecondaryContainer = AzulProfundo,
    tertiary = AmarilloPokemon,
    onTertiary = TextoCafe,
    background = FondoCrema,
    onBackground = TextoCafe,
    surface = BlancoCarta,
    onSurface = TextoCafe,
    surfaceVariant = CremaOscura,
    onSurfaceVariant = TextoSecundario,
    outline = BordeSuave,
    error = RojoError,
    onError = androidx.compose.ui.graphics.Color.White
)

private val TipografiaPokemon = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.6).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 28.sp,
        lineHeight = 33.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 23.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 21.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

private val FormasPokemon = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun Practica10Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresPokemon,
        typography = TipografiaPokemon,
        shapes = FormasPokemon,
        content = content
    )
}
