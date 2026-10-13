package com.example.practica08.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object: define las operaciones CRUD sobre la tabla de usuarios.
 *
 * - Las consultas devuelven [Flow] para que la UI (Compose) se actualice de forma
 *   reactiva cuando cambien los datos.
 * - Las operaciones de escritura son "suspend" y se ejecutan en segundo plano
 *   mediante corrutinas.
 */
@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios ORDER BY id ASC")
    fun obtenerUsuarios(): Flow<List<Usuario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario)

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Delete
    suspend fun eliminar(usuario: Usuario)
}
