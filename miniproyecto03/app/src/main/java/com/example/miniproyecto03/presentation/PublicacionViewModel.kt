package com.example.miniproyecto03.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto03.data.ImageStorage
import com.example.miniproyecto03.data.Publicacion
import com.example.miniproyecto03.data.PublicacionDao
import com.example.miniproyecto03.domain.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PublicacionViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PublicacionDao = AppDatabase.getDatabase(application).publicacionDao()

    val publicaciones: StateFlow<List<Publicacion>> =
        dao.obtenerTodas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    fun insertar(titulo: String, descripcion: String, fotoPath: String, fecha: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            _guardando.value = true
            dao.insertar(
                Publicacion(
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    fotoUri = fotoPath,
                    fecha = fecha
                )
            )
            _guardando.value = false
        }
    }

    fun eliminar(publicacion: Publicacion) {
        viewModelScope.launch { dao.eliminar(publicacion) }
    }

    fun guardarImagenBitmap(bitmap: android.graphics.Bitmap): String =
        ImageStorage.guardarBitmap(getApplication(), bitmap)

    fun guardarImagenUri(uri: android.net.Uri): String? =
        ImageStorage.guardarUri(getApplication(), uri)
}
