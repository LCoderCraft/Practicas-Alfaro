package com.example.practica08.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa la tabla "usuarios" en la base de datos SQLite.
 * Cada propiedad de la clase se convierte en una columna de la tabla.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val edad: Int
)
