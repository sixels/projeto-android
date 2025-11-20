package com.example.appruido.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CriticalNoise(
    val average: Double = 0.0,
    @ServerTimestamp
    val endedAt: Date? = null,
    @ServerTimestamp
    val startedAt: Date? = null,
    val userId: String = "",

    val level: NoiseLevel = getNoiseLevel(average),
)
