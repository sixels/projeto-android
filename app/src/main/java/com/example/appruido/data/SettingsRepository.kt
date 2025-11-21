package com.example.appruido.data

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<SettingsEntity?>
    suspend fun saveCalibration(calibrationValue: Double)
}
