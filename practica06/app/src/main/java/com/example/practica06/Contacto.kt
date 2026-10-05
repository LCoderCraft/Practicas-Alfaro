package com.example.practica06

import androidx.compose.runtime.Immutable

/**
 * Contacto.kt
 *
 * Modelo de datos de un contacto. Una lista dinamica necesita un origen de
 * datos estructurado: sin esta clase no hay nada que renderizar.
 *
 * @Immutable le indica a Compose que el objeto no cambiara, por lo que puede
 * saltarse la recomposicion de los elementos que ya son correctos.
 */
@Immutable
data class Contacto(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val ciudad: String
) {
    /** Inicial que se muestra dentro del avatar circular. */
    val inicial: String get() = nombre.trim().firstOrNull()?.uppercase() ?: "?"

    /** Formatea el telefone como 55-1234-5678 a partir de 10 digitos. */
    val telefonoFormateado: String
        get() = if (telefono.length == 10) {
            "${telefono.substring(0, 2)}-${telefono.substring(2, 6)}-${telefono.substring(6)}"
        } else {
            telefono
        }
}

/**
 * Genera 12 contactos de prueba para la sesion.
 * En una app real esta lista vendria de una API o de una base de datos;
 * aqui se simula para poder demostrar el listado y el reciclado de vistas.
 */
fun generarContactos(): List<Contacto> = listOf(
    Contacto(1, "Ana Lopez Ramirez", "5512345678", "ana.lopez@correo.com", "Ciudad de Mexico"),
    Contacto(2, "Carlos Mendoza Ruiz", "5523456789", "carlos.mendoza@correo.com", "Guadalajara"),
    Contacto(3, "Maria Fernanda Gomez", "5534567890", "maria.gomez@correo.com", "Monterrey"),
    Contacto(4, "Jorge Alberto Diaz", "5545678901", "jorge.diaz@correo.com", "Puebla"),
    Contacto(5, "Lucia Hernandez Cruz", "5556789012", "lucia.hernandez@correo.com", "Queretaro"),
    Contacto(6, "Miguel Angel Torres", "5567890123", "miguel.torres@correo.com", "Tijuana"),
    Contacto(7, "Sofia Ramirez Vega", "5578901234", "sofia.vega@correo.com", "Leon"),
    Contacto(8, "Daniel Enrique Castro", "5589012345", "daniel.castro@correo.com", "Merida"),
    Contacto(9, "Valeria Moreno Silva", "5590123456", "valeria.silva@correo.com", "Cancun"),
    Contacto(10, "Ricardo Alejandro Perez", "5501234567", "ricardo.perez@correo.com", "Toluca"),
    Contacto(11, "Gabriela Navarro Ruiz", "5512345670", "gabriela.ruiz@correo.com", "Morelia"),
    Contacto(12, "Fernando Josue Blanco", "5523456701", "fernando.blanco@correo.com", "Aguascalientes")
)