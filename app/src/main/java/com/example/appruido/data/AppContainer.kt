package com.example.appruido.data

import android.content.Context
import com.example.appruido.repository.AudioRepository
import com.google.firebase.FirebaseApp

interface AppContainer {
    val audioRepository: AudioRepository

    val firebaseRepository: Unit

    val historicoRepository: HistoricoRepository
}

class AppDataContainer(private val context: Context) : AppContainer {


    private val historicoDatabase: HistoricoDatabase by lazy {
        HistoricoDatabase.getDatabase(context)

    }
    // Implementa o Repositório usando o DAO do banco de dados (HistoricoDao)
    override val historicoRepository: HistoricoRepository by lazy {
        OfflineHistoricoRepository(historicoDatabase.historicoDao())
    }

    override val audioRepository: AudioRepository by lazy {
        AudioRepository
    }

    override val firebaseRepository by lazy {
        FirebaseApp.initializeApp(context);
        Unit
    }
}