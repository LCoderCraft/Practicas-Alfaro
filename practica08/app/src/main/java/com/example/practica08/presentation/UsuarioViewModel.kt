package com.example.practica08.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica08.data.Usuario
import com.example.practica08.data.UsuarioDao
import com.example.practica08.domain.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel que conecta la capa de persistencia (Room) con la interfaz en Compose.
 * Expone la lista de usuarios como estado reactivo y ejecuta las operaciones
 * de escritura dentro de viewModelScope.
 */
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: UsuarioDao = AppDatabase.getDatabase(application).usuarioDao()

    /** Lista de usuarios observada en tiempo real. */
    val usuarios: StateFlow<List<Usuario>> = dao.obtenerUsuarios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Inserta un nuevo usuario en la base de datos. */
    fun insertar(nombre: String, edad: Int) {
        viewModelScope.launch {
            dao.insertar(Usuario(nombre = nombre, edad = edad))
        }
    }

    /** Elimina el usuario indicado de la base de datos. */
    fun eliminar(usuario: Usuario) {
        viewModelScope.launch {
            dao.eliminar(usuario)
        }
    }
}
