package com.example.appruido.data

class CriticalNoiseDatabase private constructor() {

    val criticalNoiseDao = CriticalNoiseDao()

    companion object {
        @Volatile
        private var INSTANCE: CriticalNoiseDatabase? = null

        fun getInstance(): CriticalNoiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = CriticalNoiseDatabase()
                INSTANCE = instance
                instance
            }
        }
    }
}