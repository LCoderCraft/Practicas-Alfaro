package com.example.registroestudiantes.data

import android.content.Context
import android.content.SharedPreferences

/**
 * data/PreferencesManager.kt
 *
 * Capa de configuracion basada en pares clave-valor (SharedPreferences).
 *
 * Guarda el ultimo registro capturado para que, al reabrir la aplicacion,
 * el formulario recuerde la ultima matricula y la ultima configuracion usada.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ---------------------------------------------------------------- Lectura

    /** Ultima matricula registrada, o cadena vacia si todavia no hay ninguna. */
    fun getUltimaMatricula(): String = prefs.getString(KEY_ULTIMA_MATRICULA, "") ?: ""

    /** Ultima carrera seleccionada. */
    fun getUltimaCarrera(): String = prefs.getString(KEY_ULTIMA_CARRERA, "") ?: ""

    /** Ultimo turno seleccionado. */
    fun getUltimoTurno(): String =
        prefs.getString(KEY_ULTIMO_TURNO, Estudiante.TURNO_MATUTINO) ?: Estudiante.TURNO_MATUTINO

    /** Ultimo estatus (Switch) seleccionado. */
    fun isUltimoActivo(): Boolean = prefs.getBoolean(KEY_ULTIMO_ACTIVO, true)

    /** Devuelve el ultimo estudiante registrado, o null si no hay ninguno. */
    fun getUltimoEstudiante(): Estudiante? {
        val matricula = getUltimaMatricula()
        val nombre = prefs.getString(KEY_ULTIMO_NOMBRE, "") ?: ""
        if (matricula.isEmpty() && nombre.isEmpty()) return null
        return Estudiante(
            matricula = matricula,
            nombre = nombre,
            carrera = getUltimaCarrera(),
            turno = getUltimoTurno(),
            estatus = if (isUltimoActivo()) Estudiante.ESTATUS_ACTIVO else Estudiante.ESTATUS_INACTIVO
        )
    }

    /** Indica si ya se registro al menos un estudiante. */
    fun hayRegistro(): Boolean = getUltimaMatricula().isNotEmpty()

    // --------------------------------------------------------------- Escritura

    /**
     * Persiste el ultimo registro. apply() escribe de forma asincrona para no
     * bloquear el hilo de la interfaz.
     */
    fun guardarUltimoRegistro(estudiante: Estudiante) {
        prefs.edit()
            .putString(KEY_ULTIMA_MATRICULA, estudiante.matricula)
            .putString(KEY_ULTIMO_NOMBRE, estudiante.nombre)
            .putString(KEY_ULTIMA_CARRERA, estudiante.carrera)
            .putString(KEY_ULTIMO_TURNO, estudiante.turno)
            .putBoolean(KEY_ULTIMO_ACTIVO, estudiante.activo)
            .apply()
    }

    /**
     * Borra todas las llaves de forma sincrona (commit devuelve el resultado).
     */
    fun limpiar(): Boolean = prefs.edit().clear().commit()

    companion object {
        private const val PREFS_NAME = "registro_prefs"
        private const val KEY_ULTIMA_MATRICULA = "ultima_matricula"
        private const val KEY_ULTIMO_NOMBRE = "ultimo_nombre"
        private const val KEY_ULTIMA_CARRERA = "ultima_carrera"
        private const val KEY_ULTIMO_TURNO = "ultimo_turno"
        private const val KEY_ULTIMO_ACTIVO = "ultimo_activo"
    }
}
