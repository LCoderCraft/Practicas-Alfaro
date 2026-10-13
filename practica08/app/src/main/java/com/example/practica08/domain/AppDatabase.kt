package com.example.practica08.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.practica08.data.Usuario
import com.example.practica08.data.UsuarioDao

/**
 * Punto de acceso principal a la base de datos Room.
 * Se implementa el patron Singleton para evitar abrir multiples instancias
 * de la conexion a la base de datos al mismo tiempo.
 */
@Database(entities = [Usuario::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "practica08.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
