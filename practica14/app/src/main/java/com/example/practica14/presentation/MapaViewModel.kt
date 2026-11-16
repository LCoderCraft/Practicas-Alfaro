package com.example.practica14.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica14.data.MarcadorEntity
import com.example.practica14.data.MarcadorDao
import com.example.practica14.domain.AppDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

/**
 * ViewModel que expone la ubicacion actual, los marcadores persistidos y
 * las operaciones sobre FusedLocationProviderClient y Room.
 */
class MapaViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: MarcadorDao = AppDatabase.getDatabase(application).marcadorDao()
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    val marcadores: StateFlow<List<MarcadorEntity>> = dao.obtenerMarcadores()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val ubicacion: MutableStateFlow<GeoPoint?> = MutableStateFlow(null)

    /** Solicita una ubicacion fresca por GPS; si falla usa la ultima conocida. */
    fun obtenerUbicacionActual() {
        try {
            val token = com.google.android.gms.tasks.CancellationTokenSource().token
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        ubicacion.value = GeoPoint(location.latitude, location.longitude)
                    } else {
                        fusedClient.lastLocation.addOnSuccessListener { loc ->
                            if (loc != null) {
                                ubicacion.value = GeoPoint(loc.latitude, loc.longitude)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    fusedClient.lastLocation.addOnSuccessListener { loc ->
                        if (loc != null) {
                            ubicacion.value = GeoPoint(loc.latitude, loc.longitude)
                        }
                    }
                }
        } catch (e: SecurityException) {
            // Sin permiso de ubicacion: no se puede consultar.
        }
    }

    /** Persiste un marcador en la posicion indicada. */
    fun guardarMarcador(latitud: Double, longitud: Double, titulo: String = "Marcador") {
        viewModelScope.launch {
            dao.insertar(MarcadorEntity(latitud = latitud, longitud = longitud, titulo = titulo))
        }
    }

    /** Elimina un marcador persistido. */
    fun eliminarMarcador(marcador: MarcadorEntity) {
        viewModelScope.launch {
            dao.eliminar(marcador)
        }
    }
}
