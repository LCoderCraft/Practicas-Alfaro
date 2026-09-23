package com.example.practica04

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.practica04.components.CustomCheckbox
import com.example.practica04.components.CustomDatePicker
import com.example.practica04.components.CustomRadioGroup
import com.example.practica04.components.CustomSpinner
import com.example.practica04.components.CustomSwitch

private val levels = listOf("Basico", "Intermedio", "Avanzado")
private val cities = listOf("Ciudad de Mexico", "Guadalajara", "Monterrey", "Puebla")

@Composable
fun SectionTitle(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// Pantalla principal del formulario: integra todos los controles avanzados.
@Composable
fun FormScreen() {
    var checkedSport by remember { mutableStateOf(false) }
    var checkedReading by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(false) }
    var levelIndex by remember { mutableStateOf(0) }
    var city by remember { mutableStateOf(cities[0]) }
    var birthDate by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Formulario de Preferencias",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Practica 04 - Controles avanzados (Jetpack Compose)",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))

        SectionTitle("Pasatiempos", Icons.Filled.SportsSoccer)
        CustomCheckbox(label = "Deporte", isChecked = checkedSport) { checkedSport = it }
        CustomCheckbox(label = "Lectura", isChecked = checkedReading) { checkedReading = it }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Notificaciones", Icons.Filled.Notifications)
        CustomSwitch(label = "Recibir notificaciones", isChecked = notificationsEnabled) {
            notificationsEnabled = it
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Nivel de experiencia", Icons.Filled.School)
        CustomRadioGroup(
            options = levels,
            selectedIndex = levelIndex,
            onSelect = { levelIndex = it }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Ciudad de residencia", Icons.Filled.Place)
        CustomSpinner(
            label = "Selecciona una ciudad",
            options = cities,
            selected = city,
            onSelect = { city = it }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        SectionTitle("Fecha de nacimiento", Icons.AutoMirrored.Filled.MenuBook)
        CustomDatePicker(
            label = "Selecciona tu fecha de nacimiento",
            selectedDate = birthDate,
            onDateChange = { birthDate = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                result = buildString {
                    append("Deporte: ").append(if (checkedSport) "Si" else "No").append('\n')
                    append("Lectura: ").append(if (checkedReading) "Si" else "No").append('\n')
                    append("Notificaciones: ").append(if (notificationsEnabled) "Activadas" else "Desactivadas").append('\n')
                    append("Nivel de experiencia: ").append(levels[levelIndex]).append('\n')
                    append("Ciudad: ").append(city).append('\n')
                    append("Fecha de nacimiento: ").append(if (birthDate.isBlank()) "No seleccionada" else birthDate)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Procesar")
        }

        if (result.isNotBlank()) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen del formulario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = result, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}