package com.example.appruido.data

import android.content.Context

interface AppContainer {
    val audioRepository: AudioRepository
    val criticalNoiseRepository: CriticalNoiseRepository
    val historicoRepository: HistoricoRepository
    val settingsRepository: SettingsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val historicoRepository: HistoricoRepository by lazy {
        OfflineHistoricoRepository(appDatabase.historicoDao())
    }

    override val audioRepository: AudioRepository by lazy {
        AudioRepository(settingsRepository)
    }

    override val criticalNoiseRepository by lazy {
        CriticalNoiseFirestoreRepository(
            CriticalNoiseDatabase.getInstance().criticalNoiseDao
        )
    }

    override val settingsRepository: SettingsRepository by lazy {
        OfflineSettingsRepository(appDatabase.settingsDao())
    }
}