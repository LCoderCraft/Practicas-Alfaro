package com.example.practica12.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practica12.ui.theme.BordeSuave
import com.example.practica12.ui.theme.DoradoTcg
import com.example.practica12.ui.theme.FondoCrema
import com.example.practica12.ui.theme.RojoPokemon
import com.example.practica12.ui.theme.TextoCafe
import com.example.practica12.ui.theme.TextoSecundario
import com.example.practica12.ui.theme.prettyName
import com.example.practica12.ui.theme.typeVisual

@Composable
fun Pokeball(
    modifier: Modifier = Modifier,
    red: Color = RojoPokemon,
    ink: Color = TextoCafe
) {
    Canvas(modifier = modifier) {
        val d = size.minDimension
        val center = Offset(d / 2f, d / 2f)

        drawCircle(color = Color.White, radius = d / 2f, center = center)
        drawArc(
            color = red,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset.Zero,
            size = Size(d, d)
        )
        drawRect(
            color = ink,
            topLeft = Offset(0f, d * 0.455f),
            size = Size(d, d * 0.09f)
        )
        drawCircle(color = ink, radius = d * 0.25f, center = center)
        drawCircle(color = Color.White, radius = d * 0.165f, center = center)
        drawCircle(
            color = ink,
            radius = d / 2f,
            center = center,
            style = Stroke(width = d * 0.055f)
        )
    }
}

@Composable
fun PokeballWatermark(
    modifier: Modifier = Modifier,
    tint: Color = Color.White.copy(alpha = 0.22f)
) {
    Canvas(modifier = modifier) {
        val d = size.minDimension
        val center = Offset(d / 2f, d / 2f)
        val stroke = d * 0.045f

        drawCircle(
            color = tint,
            radius = d / 2f - stroke,
            center = center,
            style = Stroke(width = stroke)
        )
        drawLine(
            color = tint,
            start = Offset(0f, d / 2f),
            end = Offset(d, d / 2f),
            strokeWidth = stroke
        )
        drawCircle(
            color = tint,
            radius = d * 0.21f,
            center = center,
            style = Stroke(width = stroke)
        )
        drawCircle(color = tint, radius = d * 0.09f, center = center)
    }
}

@Composable
fun TypeBadge(
    typeName: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val visual = typeVisual(typeName)
    Row(
        modifier = modifier
            .background(visual.color, RoundedCornerShape(50))
            .padding(
                horizontal = if (compact) 7.dp else 9.dp,
                vertical = if (compact) 3.dp else 5.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = visual.icon,
            contentDescription = null,
            tint = visual.onColor,
            modifier = Modifier.size(if (compact) 12.dp else 14.dp)
        )
        Text(
            text = visual.label.uppercase(),
            color = visual.onColor,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = if (compact) 9.sp else 10.sp,
                letterSpacing = 0.6.sp
            )
        )
    }
}

@Composable
fun PokeballLoader(
    message: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "pokeball-loader")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing)
        ),
        label = "pokeball-angle"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 54.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Pokeball(
                modifier = Modifier
                    .padding(12.dp)
                    .size(54.dp)
                    .rotate(angle)
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = TextoCafe,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Consultando PokeAPI…",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorPane(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 28.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.errorContainer) {
                Icon(
                    imageVector = Icons.Rounded.CloudOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(14.dp)
                        .size(30.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "La Pokédex se quedó sin señal",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = RojoPokemon)
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(7.dp))
                Text("Reintentar")
            }
        }
    }
}

@Composable
fun StatBar(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (value / 180f).coerceIn(0.04f, 1f),
        animationSpec = tween(durationMillis = 700),
        label = "stat-$label"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = TextoCafe
            )
        }
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .background(Color(0xFFEDE3D2), RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(7.dp)
                    .background(color, RoundedCornerShape(50))
            )
        }
    }
}

@Composable
fun MetricTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = FondoCrema,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = DoradoTcg.copy(alpha = 0.2f)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextoCafe,
                    modifier = Modifier
                        .padding(7.dp)
                        .size(17.dp)
                )
            }
            Spacer(Modifier.width(9.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoCafe
                )
            }
        }
    }
}

@Composable
fun TypePillRow(
    types: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        types.forEach { type ->
            TypeBadge(typeName = type, compact = true)
            Text(
                text = prettyName(type),
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
