package com.example.practica12.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Landscape
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsMma
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TypeVisual(
    val key: String,
    val label: String,
    val color: Color,
    val deepColor: Color,
    val onColor: Color,
    val icon: ImageVector,
    val description: String
)

private val TintaOscura = Color(0xFF20301F)

fun typeVisual(typeName: String): TypeVisual = when (typeName.lowercase()) {
    "normal" -> TypeVisual(
        "normal", "Normal", Color(0xFFA8A77A), Color(0xFF7D7C5F), TintaOscura,
        Icons.Rounded.Circle, "Equilibrados, curiosos y llenos de recursos."
    )
    "fire" -> TypeVisual(
        "fire", "Fuego", Color(0xFFEE8130), Color(0xFFC22E0A), Color.White,
        Icons.Rounded.LocalFireDepartment, "Pasión que arde en cada batalla."
    )
    "water" -> TypeVisual(
        "water", "Agua", Color(0xFF6390F0), Color(0xFF2F5FA8), Color.White,
        Icons.Rounded.WaterDrop, "Calma y fuerza que fluyen sin descanso."
    )
    "electric" -> TypeVisual(
        "electric", "Eléctrico", Color(0xFFF7D02C), Color(0xFFC9A500), TintaOscura,
        Icons.Rounded.Bolt, "Energía que ilumina el campo en un instante."
    )
    "grass" -> TypeVisual(
        "grass", "Planta", Color(0xFF7AC74C), Color(0xFF4E8A2C), TintaOscura,
        Icons.Rounded.Eco, "Vida que crece con cada rayo de sol."
    )
    "ice" -> TypeVisual(
        "ice", "Hielo", Color(0xFF96D9D6), Color(0xFF4FA7A2), TintaOscura,
        Icons.Rounded.AcUnit, "Frío sereno, preciso y difícil de romper."
    )
    "fighting" -> TypeVisual(
        "fighting", "Lucha", Color(0xFFC22E28), Color(0xFF8E1B17), Color.White,
        Icons.Rounded.SportsMma, "Disciplina y coraje cuerpo a cuerpo."
    )
    "poison" -> TypeVisual(
        "poison", "Veneno", Color(0xFFA33EA1), Color(0xFF6E236C), Color.White,
        Icons.Rounded.Science, "Misterio que se filtra en cada rincón."
    )
    "ground" -> TypeVisual(
        "ground", "Tierra", Color(0xFFE2BF65), Color(0xFFA98A35), TintaOscura,
        Icons.Rounded.Landscape, "Firmeza que sostiene el terreno."
    )
    "flying" -> TypeVisual(
        "flying", "Volador", Color(0xFFA98FF3), Color(0xFF7157B8), Color.White,
        Icons.Rounded.Air, "Libertad que dibuja círculos sobre las nubes."
    )
    "psychic" -> TypeVisual(
        "psychic", "Psíquico", Color(0xFFF95587), Color(0xFFC72E63), Color.White,
        Icons.Rounded.Psychology, "Mentes que desafían la lógica."
    )
    "bug" -> TypeVisual(
        "bug", "Bicho", Color(0xFFA6B91A), Color(0xFF74820F), Color.White,
        Icons.Rounded.BugReport, "Pequeños héroes que trabajan en equipo."
    )
    "rock" -> TypeVisual(
        "rock", "Roca", Color(0xFFB6A136), Color(0xFF7B6D1F), TintaOscura,
        Icons.Rounded.Terrain, "Paciencia tallada en piedra."
    )
    "ghost" -> TypeVisual(
        "ghost", "Fantasma", Color(0xFF735797), Color(0xFF4B3565), Color.White,
        Icons.Rounded.NightsStay, "Sombras que guardan secretos muy antiguos."
    )
    "dragon" -> TypeVisual(
        "dragon", "Dragón", Color(0xFF6F35FC), Color(0xFF4A1DBE), Color.White,
        Icons.Rounded.AutoAwesome, "Leyendas de poder que cruzan generaciones."
    )
    "dark" -> TypeVisual(
        "dark", "Siniestro", Color(0xFF705746), Color(0xFF45332B), Color.White,
        Icons.Rounded.DarkMode, "Astucia que juega con la noche."
    )
    "steel" -> TypeVisual(
        "steel", "Acero", Color(0xFFB7B7CE), Color(0xFF737387), TintaOscura,
        Icons.Rounded.Shield, "Defensa forjada con precisión."
    )
    "fairy" -> TypeVisual(
        "fairy", "Hada", Color(0xFFD685AD), Color(0xFFA84F7B), TintaOscura,
        Icons.Rounded.Favorite, "Magia y ternura en partes iguales."
    )
    else -> TypeVisual(
        typeName, typeName.replaceFirstChar { it.uppercase() },
        Color(0xFF8B8B8B), Color(0xFF5F5F5F), Color.White,
        Icons.Rounded.Circle, "Un tipo lleno de sorpresas por descubrir."
    )
}

fun prettyName(raw: String): String = raw
    .split("-", " ")
    .filter { it.isNotBlank() }
    .joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

fun pokedexNumber(id: Int): String = "#" + id.toString().padStart(3, '0')
