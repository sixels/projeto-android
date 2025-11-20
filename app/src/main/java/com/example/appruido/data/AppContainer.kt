package com.example.appruido.data

import android.content.Context

interface AppContainer {
    val audioRepository: AudioRepository

    val criticalNoiseRepository: CriticalNoiseRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val audioRepository: AudioRepository by lazy {
        AudioRepository
    }

    override val criticalNoiseRepository by lazy {
        CriticalNoiseFirestoreRepository(
            CriticalNoiseDatabase.getInstance().criticalNoiseDao
        )
    }
}