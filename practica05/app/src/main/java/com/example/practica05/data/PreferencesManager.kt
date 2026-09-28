package com.example.practica05.data

import android.content.Context
import android.content.SharedPreferences

/**
 * data/PreferencesManager.kt
 *
 * SharedPreferences trabaja con pares clave-valor (key-value) y guarda la
 * informacion en un archivo XML interno de la app. Para acceder a ese archivo
 * necesitamos el Context de la aplicacion.
 *
 * La clase se crea una sola vez y se reutiliza desde la UI.
 */
class PreferencesManager(context: Context) {

    // Nombre del archivo interno donde se guardan las preferencias.
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ---------------------------------------------------------------- Lectura

    /** Devuelve el nombre guardado, o una cadena vacia si nunca se guardo. */
    fun getNombre(): String = prefs.getString(KEY_NOMBRE, "") ?: ""

    /** Devuelve true si las notificaciones estan activas. */
    fun areNotificacionesActivas(): Boolean = prefs.getBoolean(KEY_NOTIFICACIONES, false)

    /** Devuelve true si el usuario eligio el modo oscuro. */
    fun isModoOscuro(): Boolean = prefs.getBoolean(KEY_MODO_OSCURO, false)

    // --------------------------------------------------------------- Escritura

    /**
     * Guarda un nombre y/o banderas de preferencia.
     * apply() escribe de forma asincrona: no bloquea la UI y el dato
     * queda persistido aunque la app se cierre despues.
     */
    fun savePreferencias(
        nombre: String = getNombre(),
        notificaciones: Boolean = areNotificacionesActivas(),
        modoOscuro: Boolean = isModoOscuro()
    ) {
        prefs.edit()
            .putString(KEY_NOMBRE, nombre)
            .putBoolean(KEY_NOTIFICACIONES, notificaciones)
            .putBoolean(KEY_MODO_OSCURO, modoOscuro)
            .apply()
    }

    // ----------------------------------------------------------------- Borrado

    /**
     * Borra TODAS las llaves del archivo de preferencias.
     * commit() es sincrono y devuelve un Boolean con el exito de la operacion,
     * a diferencia de apply().
     */
    fun clearPreferencias(): Boolean = prefs.edit().clear().commit()

    /** Indica si el archivo de preferencias tiene al menos una llave. */
    fun hayDatosGuardados(): Boolean = prefs.all.isNotEmpty()

    companion object {
        private const val PREFS_NAME = "practica05_prefs"
        private const val KEY_NOMBRE = "nombre_usuario"
        private const val KEY_NOTIFICACIONES = "notificaciones_activas"
        private const val KEY_MODO_OSCURO = "modo_oscuro"
    }
}
