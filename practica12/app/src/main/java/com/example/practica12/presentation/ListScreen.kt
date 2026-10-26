package com.example.practica12.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CatchingPokemon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.practica12.data.PokemonListItem
import com.example.practica12.data.TypeItem
import com.example.practica12.ui.theme.BlancoCarta
import com.example.practica12.ui.theme.DoradoTcg
import com.example.practica12.ui.theme.FondoCrema
import com.example.practica12.ui.theme.RojoPokemon
import com.example.practica12.ui.theme.TextoCafe
import com.example.practica12.ui.theme.TextoSecundario
import com.example.practica12.ui.theme.pokedexNumber
import com.example.practica12.ui.theme.prettyName
import com.example.practica12.ui.theme.typeVisual
import com.example.practica12.presentation.components.ErrorPane
import com.example.practica12.presentation.components.Pokeball
import com.example.practica12.presentation.components.PokeballLoader
import com.example.practica12.presentation.components.PokeballWatermark
import com.example.practica12.presentation.components.TypeBadge

@Composable
fun TypeListScreen(
    state: TypesUiState,
    onTypeSelected: (String) -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFBF2), FondoCrema, Color(0xFFFFEFD5)),
                    endY = 1900f
                )
            )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                TypeHeader(typeCount = state.types.size)
            }

            when {
                state.isLoading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    PokeballLoader(message = "Abriendo la Pokédex…")
                }

                state.error != null -> item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorPane(message = state.error, onRetry = onRetry)
                }

                else -> {
                    items(state.types, key = { it.name }) { type ->
                        TypeCard(
                            type = type,
                            onClick = { onTypeSelected(type.name) }
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        FooterNote()
                    }
                }
            }
        }
    }
}

@Composable
private fun TypeHeader(typeCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 5.dp
            ) {
                Pokeball(
                    modifier = Modifier
                        .padding(7.dp)
                        .size(44.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "POKÉDEX TCG",
                    style = MaterialTheme.typography.labelLarge,
                    color = RojoPokemon,
                    letterSpacing = 1.8.sp
                )
                Text(
                    text = "Práctica 12 · API REST",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = DoradoTcg.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, DoradoTcg.copy(alpha = 0.45f))
            ) {
                Text(
                    text = "$typeCount tipos",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoCafe
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¿Qué tipo\neliges hoy?",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextoCafe
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Cada tipo es una puerta distinta. Elige uno y descubre a sus Pokémon.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoSecundario
                )
            }
            PokeballWatermark(
                modifier = Modifier
                    .size(88.dp)
                    .offset(x = 8.dp),
                tint = RojoPokemon.copy(alpha = 0.09f)
            )
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.8f),
            border = BorderStroke(1.dp, Color(0xFFE8DAC3))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(Color(0xFF2E8B57), CircleShape)
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = "Datos en vivo desde PokeAPI",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario
                )
            }
        }
    }
}

@Composable
private fun TypeCard(
    type: TypeItem,
    onClick: () -> Unit
) {
    val visual = typeVisual(type.name)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = visual.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(visual.color, visual.deepColor)
                    )
                )
        ) {
            PokeballWatermark(
                modifier = Modifier
                    .size(102.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 20.dp),
                tint = visual.onColor.copy(alpha = 0.16f)
            )
            Text(
                text = "#" + type.id.toString().padStart(2, '0'),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(13.dp),
                style = MaterialTheme.typography.labelMedium,
                color = visual.onColor.copy(alpha = 0.55f)
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Surface(
                    shape = CircleShape,
                    color = visual.onColor.copy(alpha = 0.17f)
                ) {
                    Icon(
                        imageVector = visual.icon,
                        contentDescription = null,
                        tint = visual.onColor,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = visual.label,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                    color = visual.onColor
                )
                Text(
                    text = "Explorar",
                    style = MaterialTheme.typography.labelMedium,
                    color = visual.onColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    typeName: String,
    state: PokemonListUiState,
    onBack: () -> Unit,
    onPokemonSelected: (String) -> Unit,
    onRetry: () -> Unit
) {
    val visual = typeVisual(typeName)
    Scaffold(
        containerColor = FondoCrema,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = visual.icon,
                            contentDescription = null,
                            tint = visual.deepColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = visual.label,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Volver a los tipos"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FondoCrema,
                    titleContentColor = TextoCafe,
                    navigationIconContentColor = TextoCafe
                )
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PokemonListHeader(
                    typeName = typeName,
                    count = state.pokemon.size,
                    loaded = state.loaded
                )
            }

            when {
                state.isLoading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    PokeballLoader(message = "Buscando Pokémon de tipo ${visual.label}…")
                }

                state.error != null -> item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorPane(message = state.error, onRetry = onRetry)
                }

                state.pokemon.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Este tipo aún no tiene Pokémon registrados.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextoSecundario
                    )
                }

                else -> {
                    items(state.pokemon, key = { it.name }) { pokemon ->
                        PokemonGridCard(
                            pokemon = pokemon,
                            typeName = typeName,
                            onClick = { onPokemonSelected(pokemon.name) }
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Ilustraciones oficiales de PokeAPI · ${state.pokemon.size} entradas",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextoSecundario
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonListHeader(
    typeName: String,
    count: Int,
    loaded: Boolean
) {
    val visual = typeVisual(typeName)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(28.dp),
        color = visual.deepColor,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(visual.color, visual.deepColor)
                    )
                )
                .padding(20.dp)
        ) {
            PokeballWatermark(
                modifier = Modifier
                    .size(132.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 30.dp),
                tint = visual.onColor.copy(alpha = 0.14f)
            )
            Column(modifier = Modifier.fillMaxWidth(0.78f)) {
                Text(
                    text = "TIPO ${visual.label.uppercase()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = visual.onColor.copy(alpha = 0.75f),
                    letterSpacing = 1.4.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = visual.description,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 21.sp),
                    color = visual.onColor
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = if (loaded) "$count Pokémon" else "Cargando…",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = visual.onColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PokemonGridCard(
    pokemon: PokemonListItem,
    typeName: String,
    onClick: () -> Unit
) {
    val visual = typeVisual(typeName)
    val context = LocalContext.current
    val request = remember(pokemon.imageUrl) {
        ImageRequest.Builder(context)
            .data(pokemon.imageUrl)
            .crossfade(220)
            .build()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoCarta),
        border = BorderStroke(1.dp, visual.color.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(116.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(visual.color.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
            ) {
                PokeballWatermark(
                    modifier = Modifier
                        .size(86.dp)
                        .align(Alignment.Center),
                    tint = visual.color.copy(alpha = 0.14f)
                )
                AsyncImage(
                    model = request,
                    contentDescription = "Ilustración de ${prettyName(pokemon.name)}",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    contentScale = ContentScale.Fit
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(7.dp),
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.86f)
                ) {
                    Text(
                        text = pokedexNumber(pokemon.id),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = visual.deepColor
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = prettyName(pokemon.name),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoCafe,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(5.dp))
                TypeBadge(typeName = typeName, compact = true)
            }
        }
    }
}

@Composable
private fun FooterNote() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.CatchingPokemon,
            contentDescription = null,
            tint = TextoSecundario.copy(alpha = 0.55f),
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "18 tipos · datos abiertos de PokeAPI",
            style = MaterialTheme.typography.labelMedium,
            color = TextoSecundario
        )
    }
}
