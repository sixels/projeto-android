package com.example.appruido.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appruido.repository.AudioRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class
HomeScreenViewModel(audioRepository: AudioRepository) : ViewModel() {
    @OptIn(FlowPreview::class)
    val decibels: StateFlow<Double> =
        audioRepository.decibels
            .sample(333)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = 0.0
            )

    private val _history = MutableStateFlow<List<Double>>(emptyList())
    val history: StateFlow<List<Double>> = _history

    init {
        // Every time decibels emits, update the history buffer
        viewModelScope.launch {
            decibels.collect { value ->
                var dbValue = value.absoluteValue
                if (dbValue.isInfinite()) {
                    dbValue = 0.0
                }


                val updated = _history.value
                    .plus(dbValue)
                    .takeLast(25)

                _history.value = updated
            }
        }
    }


    private val _isRunning = MutableStateFlow(audioRepository.isRecording)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun setIsRunning(value: Boolean) {
        _isRunning.value = value
    }
}
