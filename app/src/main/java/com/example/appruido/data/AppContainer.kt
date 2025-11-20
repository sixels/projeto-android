package com.example.appruido.data

import android.content.Context
import com.example.appruido.data.AudioRepository
import com.google.firebase.FirebaseApp

interface AppContainer {
    val audioRepository: AudioRepository

    val firebaseRepository: Unit
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val audioRepository: AudioRepository by lazy {
        AudioRepository
    }

    override val firebaseRepository by lazy {
        FirebaseApp.initializeApp(context);
        Unit
    }
}