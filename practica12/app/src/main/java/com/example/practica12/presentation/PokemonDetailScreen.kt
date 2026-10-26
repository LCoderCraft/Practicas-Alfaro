package com.example.practica12.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Scale
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.practica12.data.PokemonCard
import com.example.practica12.ui.theme.BlancoCarta
import com.example.practica12.ui.theme.BordeSuave
import com.example.practica12.ui.theme.CremaOscura
import com.example.practica12.ui.theme.DoradoTcg
import com.example.practica12.ui.theme.FondoCrema
import com.example.practica12.ui.theme.RojoPokemon
import com.example.practica12.ui.theme.TextoCafe
import com.example.practica12.ui.theme.TextoSecundario
import com.example.practica12.ui.theme.pokedexNumber
import com.example.practica12.ui.theme.prettyName
import com.example.practica12.ui.theme.typeVisual
import com.example.practica12.presentation.components.ErrorPane
import com.example.practica12.presentation.components.MetricTile
import com.example.practica12.presentation.components.PokeballLoader
import com.example.practica12.presentation.components.PokeballWatermark
import com.example.practica12.presentation.components.StatBar
import com.example.practica12.presentation.components.TypeBadge
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    state: PokemonDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    val pokemon = state.pokemon
    val visual = typeVisual(pokemon?.types?.firstOrNull() ?: "normal")

    Scaffold(
        containerColor = FondoCrema,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pokemon?.let { prettyName(it.name) } ?: "Carta Pokémon",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Volver a la lista"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = TextoCafe,
                    navigationIconContentColor = TextoCafe
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            visual.color.copy(alpha = 0.32f),
                            FondoCrema,
                            FondoCrema
                        ),
                        endY = 980f
                    )
                )
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    PokeballLoader(
                        message = "Invocando a ${prettyName(state.name)}…"
                    )
                }

                state.error != null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorPane(message = state.error, onRetry = onRetry)
                }

                pokemon != null -> PokemonDetailContent(pokemon = pokemon)
            }
        }
    }
}

@Composable
private fun PokemonDetailContent(pokemon: PokemonCard) {
    val visual = typeVisual(pokemon.types.firstOrNull() ?: "normal")
    val context = LocalContext.current
    val request = remember(pokemon.imageUrl) {
        ImageRequest.Builder(context)
            .data(pokemon.imageUrl)
            .crossfade(260)
            .build()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 2.dp, bottom = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "CARTA COLECCIONABLE · POKÉDEX TCG",
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario,
                letterSpacing = 1.5.sp
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 430.dp)
                    .shadow(18.dp, RoundedCornerShape(30.dp))
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFFFFF0B8),
                                DoradoTcg,
                                Color(0xFFB88624),
                                Color(0xFFFFE8A3)
                            )
                        ),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(3.dp)
                    .background(
                        Brush.linearGradient(listOf(visual.color, visual.deepColor)),
                        RoundedCornerShape(27.dp)
                    )
                    .padding(9.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(19.dp))
                        .background(BlancoCarta)
                        .padding(15.dp)
                ) {
                    CardHeader(pokemon)
                    Spacer(Modifier.height(9.dp))
                    CardTypeLine(pokemon)
                    Spacer(Modifier.height(10.dp))
                    ArtworkFrame(pokemon, request)
                    Spacer(Modifier.height(11.dp))
                    Text(
                        text = visual.description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = TextoSecundario
                    )
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = DoradoTcg.copy(alpha = 0.55f))
                    Spacer(Modifier.height(11.dp))
                    Text(
                        text = "DATOS DE LA POKÉDEX",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoSecundario,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricTile(
                            icon = Icons.Rounded.Straighten,
                            label = "Altura",
                            value = String.format(Locale.US, "%.1f m", pokemon.heightMeters),
                            modifier = Modifier.weight(1f)
                        )
                        MetricTile(
                            icon = Icons.Rounded.Scale,
                            label = "Peso",
                            value = String.format(Locale.US, "%.1f kg", pokemon.weightKg),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    AbilityList(pokemon.abilities)
                    Spacer(Modifier.height(13.dp))
                    HorizontalDivider(color = BordeSuave)
                    Spacer(Modifier.height(12.dp))
                    StatsSection(pokemon, visual.color)
                    Spacer(Modifier.height(13.dp))
                    CardFooter(pokemon)
                }
            }
        }

        item {
            Text(
                text = "Los datos de esta carta se consultaron en vivo a PokeAPI.",
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CardHeader(pokemon: PokemonCard) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Surface(
                shape = RoundedCornerShape(50),
                color = CremaOscura
            ) {
                Text(
                    text = "BÁSICO",
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario,
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(5.dp))
            Text(
                text = prettyName(pokemon.name),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoCafe
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "HP",
                style = MaterialTheme.typography.labelMedium,
                color = RojoPokemon,
                fontWeight = FontWeight.Black
            )
            Text(
                text = pokemon.hp.toString(),
                style = MaterialTheme.typography.headlineLarge,
                color = RojoPokemon
            )
        }
    }
}

@Composable
private fun CardTypeLine(pokemon: PokemonCard) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            pokemon.types.forEach { type ->
                TypeBadge(typeName = type, compact = true)
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = pokedexNumber(pokemon.id),
            style = MaterialTheme.typography.labelMedium,
            color = TextoSecundario
        )
    }
}

@Composable
private fun ArtworkFrame(
    pokemon: PokemonCard,
    request: ImageRequest
) {
    val visual = typeVisual(pokemon.types.firstOrNull() ?: "normal")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(252.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        visual.color.copy(alpha = 0.18f),
                        visual.deepColor.copy(alpha = 0.08f)
                    )
                )
            )
            .border(3.dp, Color(0xFFE9D9B8), RoundedCornerShape(16.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = this.center
            drawCircle(
                color = visual.color.copy(alpha = 0.13f),
                radius = size.minDimension * 0.39f,
                center = center
            )
            drawCircle(
                color = visual.color.copy(alpha = 0.16f),
                radius = size.minDimension * 0.30f,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30).toDouble())
                val start = androidx.compose.ui.geometry.Offset(
                    center.x + kotlin.math.cos(angle).toFloat() * size.minDimension * 0.32f,
                    center.y + kotlin.math.sin(angle).toFloat() * size.minDimension * 0.32f
                )
                val end = androidx.compose.ui.geometry.Offset(
                    center.x + kotlin.math.cos(angle).toFloat() * size.minDimension * 0.43f,
                    center.y + kotlin.math.sin(angle).toFloat() * size.minDimension * 0.43f
                )
                drawLine(
                    color = visual.color.copy(alpha = 0.12f),
                    start = start,
                    end = end,
                    strokeWidth = 2f
                )
            }
        }
        PokeballWatermark(
            modifier = Modifier
                .size(176.dp)
                .align(Alignment.Center),
            tint = visual.color.copy(alpha = 0.16f)
        )
        AsyncImage(
            model = request,
            contentDescription = "Ilustración oficial de ${prettyName(pokemon.name)}",
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.88f)
        ) {
            Text(
                text = pokedexNumber(pokemon.id),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = visual.deepColor
            )
        }
    }
}

@Composable
private fun AbilityList(abilities: List<String>) {
    if (abilities.isEmpty()) return
    Text(
        text = "HABILIDADES",
        style = MaterialTheme.typography.labelMedium,
        color = TextoSecundario,
        letterSpacing = 1.2.sp
    )
    Spacer(Modifier.height(6.dp))
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        abilities.forEach { ability ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(DoradoTcg, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = ability,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoCafe
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    pokemon: PokemonCard,
    color: Color
) {
    val total = pokemon.hp + pokemon.attack + pokemon.defense +
        pokemon.specialAttack + pokemon.specialDefense + pokemon.speed

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "PUNTOS DE COMBATE",
            style = MaterialTheme.typography.labelMedium,
            color = TextoSecundario,
            letterSpacing = 1.2.sp
        )
        Spacer(Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(50),
            color = color.copy(alpha = 0.13f),
            border = BorderStroke(1.dp, color.copy(alpha = 0.28f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "TOTAL $total",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoCafe
                )
            }
        }
    }
    Spacer(Modifier.height(9.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StatBar("PS", pokemon.hp, color)
        StatBar("Ataque", pokemon.attack, color)
        StatBar("Defensa", pokemon.defense, color)
        StatBar("At. Esp.", pokemon.specialAttack, color)
        StatBar("Def. Esp.", pokemon.specialDefense, color)
        StatBar("Velocidad", pokemon.speed, color)
    }
}

@Composable
private fun CardFooter(pokemon: PokemonCard) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "PokéAPI · ${pokedexNumber(pokemon.id)}",
            style = MaterialTheme.typography.labelMedium,
            color = TextoSecundario
        )
        Spacer(Modifier.weight(1f))
        Surface(
            shape = RoundedCornerShape(50),
            color = DoradoTcg.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, DoradoTcg.copy(alpha = 0.55f))
        ) {
            Text(
                text = "TCG",
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelMedium,
                color = TextoCafe,
                letterSpacing = 1.sp
            )
        }
    }
}
