package com.example.appruido.data

class CriticalNoiseFirestoreRepository(private val criticalNoiseDao: CriticalNoiseDao) : CriticalNoiseRepository {

    override suspend fun insert(criticalNoise: CriticalNoise) {
        criticalNoiseDao.insert(criticalNoise)
    }

    override suspend fun getAll(userId: String): List<CriticalNoise> {
        return criticalNoiseDao.getAll(userId)
    }
}