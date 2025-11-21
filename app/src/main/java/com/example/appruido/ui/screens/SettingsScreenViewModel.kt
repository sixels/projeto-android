package com.example.appruido.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appruido.data.AudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

enum class CalibrationState {
    IDLE,
    CALIBRATING,
    FINISHED
}

class SettingsScreenViewModel(private val audioRepository: AudioRepository) : ViewModel() {

    private val _calibrationState = MutableStateFlow(CalibrationState.IDLE)
    val calibrationState: StateFlow<CalibrationState> = _calibrationState

    private val _calibrationValue = MutableStateFlow<Double?>(null)
    val calibrationValue: StateFlow<Double?> = _calibrationValue

    fun startCalibration() {
        viewModelScope.launch {
            if (_calibrationState.value == CalibrationState.CALIBRATING) return@launch

            _calibrationState.value = CalibrationState.CALIBRATING
            _calibrationValue.value = null

            audioRepository.start()

            val samples = audioRepository.decibels
                .take(10)
                .toList()

            audioRepository.stop()

            val average = samples.filter { it > 0 }.average()
            val adjustment = if (average.isNaN()) 0.0 else 33.0 - average

            audioRepository.adjustCalibration(adjustment)

            _calibrationValue.value = adjustment
            _calibrationState.value = CalibrationState.FINISHED
        }
    }
}