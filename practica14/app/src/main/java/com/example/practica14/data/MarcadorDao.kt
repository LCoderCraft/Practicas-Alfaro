package com.example.practica14.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO de marcadores. Las consultas devuelven Flow para actualizacion reactiva
 * en Compose; las escrituras son suspend.
 */
@Dao
interface MarcadorDao {

    @Query("SELECT * FROM marcadores ORDER BY id ASC")
    fun obtenerMarcadores(): Flow<List<MarcadorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(marcador: MarcadorEntity)

    @Delete
    suspend fun eliminar(marcador: MarcadorEntity)
}
