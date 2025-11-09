package com.example.appruido.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appruido.repository.AudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn

class HomeScreenViewModel : ViewModel() {

    val decibels: StateFlow<Double> =
        AudioRepository.decibels
            .sample(300)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )

    private val _isRunning = MutableStateFlow(AudioRepository.isRecording)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun setIsRunning(value: Boolean) {
        _isRunning.value = value
    }
}
