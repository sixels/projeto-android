package com.example.appruido

import android.app.Application
import com.google.firebase.FirebaseApp

class ApplicationEntrypoint : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
    }
}