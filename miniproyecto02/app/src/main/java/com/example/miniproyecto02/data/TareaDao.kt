package com.example.miniproyecto02.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la gestion de tareas.
 *
 * - La consulta devuelve [Flow] para que la UI se actualice de forma reactiva.
 * - Las operaciones de escritura son "suspend" (corrutinas en segundo plano).
 */
@Dao
interface TareaDao {

    @Query("SELECT * FROM tareas ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: Tarea)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)

    @Query("UPDATE tareas SET completada = :completada WHERE id = :id")
    suspend fun actualizarEstado(id: Int, completada: Boolean)

    @Query("DELETE FROM tareas WHERE completada = 1")
    suspend fun eliminarCompletadas()
}
