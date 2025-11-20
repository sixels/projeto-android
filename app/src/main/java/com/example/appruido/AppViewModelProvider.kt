package com.example.appruido

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appruido.ui.screens.HomeScreenViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeScreenViewModel(
                application().container.audioRepository
            )
        }

    }
}


fun CreationExtras.application(): ApplicationEntrypoint =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ApplicationEntrypoint)
