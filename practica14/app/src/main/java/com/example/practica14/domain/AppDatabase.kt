package com.example.practica14.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.practica14.data.MarcadorDao
import com.example.practica14.data.MarcadorEntity

/**
 * Base de datos Room (patron Singleton) para los marcadores del mapa.
 */
@Database(entities = [MarcadorEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun marcadorDao(): MarcadorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "practica14.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
