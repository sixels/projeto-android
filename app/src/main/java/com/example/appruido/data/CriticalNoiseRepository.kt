package com.example.appruido.data

interface CriticalNoiseRepository {
    suspend fun getAll(userId: String): List<CriticalNoise>
    suspend fun insert(criticalNoise: CriticalNoise)
}
