package com.example.appruido.data

import kotlinx.coroutines.flow.Flow

class OfflineSettingsRepository(private val settingsDao: SettingsDao) : SettingsRepository {
    override fun getSettings(): Flow<SettingsEntity?> = settingsDao.getSettings()

    override suspend fun saveCalibration(calibrationValue: Double) {
        settingsDao.upsert(SettingsEntity(calibration = calibrationValue))
    }
}
