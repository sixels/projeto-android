package com.example.appruido

import android.app.Application
import com.example.appruido.data.AppContainer
import com.example.appruido.data.AppDataContainer

class ApplicationEntrypoint : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}