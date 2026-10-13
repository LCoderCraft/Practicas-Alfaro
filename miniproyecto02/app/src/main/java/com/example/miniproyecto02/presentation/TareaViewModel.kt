package com.example.miniproyecto02.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto02.data.Tarea
import com.example.miniproyecto02.data.TareaDao
import com.example.miniproyecto02.domain.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Opciones de filtrado de la lista. */
enum class FiltroTareas { TODAS, PENDIENTES, COMPLETADAS }

/** Criterios de ordenamiento de la lista. */
enum class OrdenTareas { FECHA, TITULO, ESTADO }

/**
 * ViewModel que conecta Room con la interfaz en Compose.
 * Expone la lista de tareas ya filtrada y ordenada como estado reactivo,
 * y ejecuta las operaciones de escritura dentro de viewModelScope.
 */
class TareaViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: TareaDao = AppDatabase.getDatabase(application).tareaDao()

    private val _filtro = MutableStateFlow(FiltroTareas.TODAS)
    val filtro: StateFlow<FiltroTareas> = _filtro.asStateFlow()

    private val _orden = MutableStateFlow(OrdenTareas.FECHA)
    val orden: StateFlow<OrdenTareas> = _orden.asStateFlow()

    val tareas: StateFlow<List<Tarea>> =
        combine(dao.obtenerTodas(), _filtro, _orden) { lista, filtro, orden ->
            val filtrada = when (filtro) {
                FiltroTareas.TODAS -> lista
                FiltroTareas.PENDIENTES -> lista.filter { !it.completada }
                FiltroTareas.COMPLETADAS -> lista.filter { it.completada }
            }
            when (orden) {
                OrdenTareas.FECHA -> filtrada.sortedByDescending { it.fecha }
                OrdenTareas.TITULO -> filtrada.sortedBy { it.titulo.lowercase() }
                OrdenTareas.ESTADO -> filtrada.sortedBy { it.completada }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun insertar(titulo: String, descripcion: String) {
        viewModelScope.launch {
            dao.insertar(Tarea(titulo = titulo.trim(), descripcion = descripcion.trim()))
        }
    }

    fun actualizar(tarea: Tarea) {
        viewModelScope.launch { dao.actualizar(tarea) }
    }

    fun alternarCompletada(tarea: Tarea) {
        viewModelScope.launch { dao.actualizarEstado(tarea.id, !tarea.completada) }
    }

    fun eliminar(tarea: Tarea) {
        viewModelScope.launch { dao.eliminar(tarea) }
    }

    fun eliminarCompletadas() {
        viewModelScope.launch { dao.eliminarCompletadas() }
    }

    fun cambiarFiltro(filtro: FiltroTareas) {
        _filtro.value = filtro
    }

    fun cambiarOrden(orden: OrdenTareas) {
        _orden.value = orden
    }
}
