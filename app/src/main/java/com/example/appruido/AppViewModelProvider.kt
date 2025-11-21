package com.example.appruido

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appruido.ui.screens.HistoricoScreenViewModel
import com.example.appruido.ui.screens.HomeScreenViewModel
import com.example.appruido.ui.screens.SettingsScreenViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeScreenViewModel(
                audioRepository = application().container.audioRepository,
                criticalNoiseRepository =  application().container.criticalNoiseRepository,
                historicoRepository = application().container.historicoRepository
            )
        }

        initializer {
            HistoricoScreenViewModel(
                historicoRepository = application().container.historicoRepository
            )
        }

        initializer {
            SettingsScreenViewModel(
                audioRepository = application().container.audioRepository
            )
        }

    }
}


fun CreationExtras.application(): ApplicationEntrypoint =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ApplicationEntrypoint)
