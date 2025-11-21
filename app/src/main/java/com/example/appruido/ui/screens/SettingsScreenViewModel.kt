package com.example.appruido.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appruido.data.AudioRepository
import com.example.appruido.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

enum class CalibrationState {
    IDLE,
    CALIBRATING,
    FINISHED
}

class SettingsScreenViewModel(
    private val audioRepository: AudioRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _calibrationState = MutableStateFlow(CalibrationState.IDLE)
    val calibrationState: StateFlow<CalibrationState> = _calibrationState.asStateFlow()

    private val _calibrationValue = MutableStateFlow<Double?>(null)
    val calibrationValue: StateFlow<Double?> = _calibrationValue.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings().first()
            settings?.let {
                _calibrationValue.value = it.calibration
                audioRepository.adjustCalibration(it.calibration)
            }
        }
    }

    fun startCalibration() {
        viewModelScope.launch {
            if (_calibrationState.value == CalibrationState.CALIBRATING) return@launch

            // 1. Verifica se a medição estava ativa antes de calibrar
            val wasRecording = audioRepository.isRecording.first()
            if (wasRecording) {
                audioRepository.stop()
            }

            _calibrationState.value = CalibrationState.CALIBRATING
            _calibrationValue.value = null

            // Usa o start/stop do repositório para a calibração
            audioRepository.start()

            val samples = audioRepository.decibels
                .take(10)
                .toList()

            audioRepository.stop()

            val average = samples.filter { it.isFinite() && it > 0 }.average()
            val adjustment = if (average.isNaN()) 0.0 else 33.0 - average

            audioRepository.adjustCalibration(adjustment)
            settingsRepository.saveCalibration(adjustment)

            _calibrationValue.value = adjustment
            _calibrationState.value = CalibrationState.FINISHED

            // 2. Se estava gravando antes, reinicia a medição
            if (wasRecording) {
                audioRepository.start()
            }
        }
    }
}