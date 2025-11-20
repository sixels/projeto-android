package com.example.appruido.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoricoEntity::class], version = 1, exportSchema = false)
abstract class HistoricoDatabase : RoomDatabase() {

    abstract fun historicoDao(): HistoricoDao

    companion object {
        @Volatile
        private var Instance: HistoricoDatabase? = null

        fun getDatabase(context: Context): HistoricoDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, HistoricoDatabase::class.java, "historico_database")
                    .build().also { Instance = it }
            }
        }
    }
}