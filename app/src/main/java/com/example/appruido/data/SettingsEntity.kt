package com.example.appruido.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1, // Singleton settings row
    val calibration: Double = 0.0
)
