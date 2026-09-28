package com.example.registroestudiantes.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * data/EstudianteRepository.kt
 *
 * Persistencia de la coleccion de estudiantes.
 *
 * SharedPreferences solo maneja clave -> valor simple, asi que la lista completa
 * se serializa a JSON y se guarda en una sola llave. Al leer se deserializa
 * de vuelta. org.json viene incluido en Android, no requiere dependencias extra.
 */
class EstudianteRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Devuelve todos los estudiantes registrados, del mas reciente al mas antiguo. */
    fun obtenerTodos(): List<Estudiante> {
        val json = prefs.getString(KEY_LISTA, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                Estudiante(
                    matricula = o.getString("matricula"),
                    nombre = o.getString("nombre"),
                    carrera = o.optString("carrera"),
                    turno = o.optString("turno"),
                    estatus = o.optString("estatus")
                )
            }
        }.getOrElse { emptyList() }
    }

    /**
     * Agrega un estudiante al inicio de la lista y persiste el JSON.
     * Devuelve el total de registros almacenados.
     */
    fun agregar(estudiante: Estudiante): Int {
        val actualizados = listOf(estudiante) + obtenerTodos()
        guardar(actualizados)
        return actualizados.size
    }

    /** Busca un estudiante por su matricula. */
    fun buscarPorMatricula(matricula: String): Estudiante? =
        obtenerTodos().firstOrNull { it.matricula.equals(matricula, ignoreCase = true) }

    /** Elimina todos los estudiantes. */
    fun eliminarTodos(): Boolean = prefs.edit().remove(KEY_LISTA).commit()

    /** Indica si la lista tiene al menos un elemento. */
    fun hayRegistros(): Boolean = prefs.getString(KEY_LISTA, null)?.isNotBlank() == true

    private fun guardar(estudiantes: List<Estudiante>) {
        val array = JSONArray()
        estudiantes.forEach { e ->
            array.put(
                JSONObject().apply {
                    put("matricula", e.matricula)
                    put("nombre", e.nombre)
                    put("carrera", e.carrera)
                    put("turno", e.turno)
                    put("estatus", e.estatus)
                }
            )
        }
        prefs.edit().putString(KEY_LISTA, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "registro_datos"
        private const val KEY_LISTA = "estudiantes_json"
    }
}
