package com.example.miniproyecto02.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miniproyecto02.data.Tarea
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Pantalla principal del Administrador de Tareas Locales.
 * ToolBar con menu de opciones (ordenar, filtrar, borrar completadas),
 * listado dinamico (LazyColumn) y boton flotante para crear tareas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TareaViewModel = viewModel()) {

    val tareas by viewModel.tareas.collectAsState()
    val filtro by viewModel.filtro.collectAsState()
    val orden by viewModel.orden.collectAsState()

    var menuAbierto by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var tareaEnEdicion by remember { mutableStateOf<Tarea?>(null) }
    var confirmarBorrado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administrador de Tareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = { menuAbierto = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        EncabezadoMenu("Ordenar por")
                        OpcionMenu("Fecha", orden == OrdenTareas.FECHA) {
                            viewModel.cambiarOrden(OrdenTareas.FECHA); menuAbierto = false
                        }
                        OpcionMenu("Titulo", orden == OrdenTareas.TITULO) {
                            viewModel.cambiarOrden(OrdenTareas.TITULO); menuAbierto = false
                        }
                        OpcionMenu("Estado (pendientes primero)", orden == OrdenTareas.ESTADO) {
                            viewModel.cambiarOrden(OrdenTareas.ESTADO); menuAbierto = false
                        }
                        HorizontalDivider()
                        EncabezadoMenu("Filtrar")
                        OpcionMenu("Todas", filtro == FiltroTareas.TODAS) {
                            viewModel.cambiarFiltro(FiltroTareas.TODAS); menuAbierto = false
                        }
                        OpcionMenu("Pendientes", filtro == FiltroTareas.PENDIENTES) {
                            viewModel.cambiarFiltro(FiltroTareas.PENDIENTES); menuAbierto = false
                        }
                        OpcionMenu("Completadas", filtro == FiltroTareas.COMPLETADAS) {
                            viewModel.cambiarFiltro(FiltroTareas.COMPLETADAS); menuAbierto = false
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Borrar completadas") },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            },
                            onClick = {
                                menuAbierto = false
                                confirmarBorrado = true
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                tareaEnEdicion = null
                mostrarDialogo = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Accesos rapidos de filtrado.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtro == FiltroTareas.TODAS,
                    onClick = { viewModel.cambiarFiltro(FiltroTareas.TODAS) },
                    label = { Text("Todas") }
                )
                FilterChip(
                    selected = filtro == FiltroTareas.PENDIENTES,
                    onClick = { viewModel.cambiarFiltro(FiltroTareas.PENDIENTES) },
                    label = { Text("Pendientes") }
                )
                FilterChip(
                    selected = filtro == FiltroTareas.COMPLETADAS,
                    onClick = { viewModel.cambiarFiltro(FiltroTareas.COMPLETADAS) },
                    label = { Text("Completadas") }
                )
            }

            Text(
                text = "Tareas: ${tareas.size}  |  Orden: ${nombreOrden(orden)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (tareas.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No hay tareas para mostrar.\nAgrega una con el boton +",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Listado dinamico y reactivo de tareas.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                ) {
                    items(tareas, key = { it.id }) { tarea ->
                        TareaItem(
                            tarea = tarea,
                            onAlternar = { viewModel.alternarCompletada(tarea) },
                            onEditar = {
                                tareaEnEdicion = tarea
                                mostrarDialogo = true
                            },
                            onEliminar = { viewModel.eliminar(tarea) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoTarea(
            tarea = tareaEnEdicion,
            onGuardar = { titulo, descripcion ->
                val actual = tareaEnEdicion
                if (actual == null) {
                    viewModel.insertar(titulo, descripcion)
                } else {
                    viewModel.actualizar(
                        actual.copy(titulo = titulo.trim(), descripcion = descripcion.trim())
                    )
                }
            },
            onCerrar = { mostrarDialogo = false }
        )
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("Borrar completadas") },
            text = { Text("Se eliminaran todas las tareas marcadas como completadas. ¿Continuar?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarCompletadas()
                    confirmarBorrado = false
                }) { Text("Borrar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun EncabezadoMenu(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun OpcionMenu(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(texto) },
        onClick = onClick,
        leadingIcon = {
            if (seleccionado) {
                Icon(Icons.Default.Check, contentDescription = null)
            }
        }
    )
}

@Composable
private fun TareaItem(
    tarea: Tarea,
    onAlternar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = tarea.completada, onCheckedChange = { onAlternar() })

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else TextDecoration.None
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = formatearFecha(tarea.fecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar ${tarea.titulo}")
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar ${tarea.titulo}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun DialogoTarea(
    tarea: Tarea?,
    onGuardar: (String, String) -> Unit,
    onCerrar: () -> Unit
) {
    var titulo by remember { mutableStateOf(tarea?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(tarea?.descripcion ?: "") }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (tarea == null) "Nueva tarea" else "Editar tarea") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Titulo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripcion") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (titulo.isNotBlank()) {
                        onGuardar(titulo, descripcion)
                        onCerrar()
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}

private fun nombreOrden(orden: OrdenTareas): String = when (orden) {
    OrdenTareas.FECHA -> "Fecha"
    OrdenTareas.TITULO -> "Titulo"
    OrdenTareas.ESTADO -> "Estado"
}

private fun formatearFecha(millis: Long): String =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(millis))
