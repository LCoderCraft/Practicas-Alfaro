package com.example.registroestudiantes.data

import androidx.compose.runtime.Immutable

/**
 * Modelo de datos de un estudiante registrado.
 *
 * Se usa @Immutable para que Compose sepa que el objeto no cambia y pueda
 * optimizar la recomposicion de la UI.
 */
@Immutable
data class Estudiante(
    val matricula: String = "",
    val nombre: String = "",
    val carrera: String = "",
    val turno: String = "",
    val estatus: String = ""
) {
    /** El estatus se maneja como Switch en la UI, pero se guarda como texto. */
    val activo: Boolean get() = estatus == ESTATUS_ACTIVO

    companion object {
        const val TURNO_MATUTINO = "Matutino"
        const val TURNO_VESPERTINO = "Vespertino"

        const val ESTATUS_ACTIVO = "Activo"
        const val ESTATUS_INACTIVO = "Inactivo"

        val CARRERAS = listOf(
            "Ingeniería en Sistemas",
            "Ingeniería Industrial",
            "Licenciatura en Administración",
            "Licenciatura en Contaduría",
            "Analista en Sistemas"
        )

        val TURNOS = listOf(TURNO_MATUTINO, TURNO_VESPERTINO)
    }
}
