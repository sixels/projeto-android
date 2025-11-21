package com.example.appruido.ui.screens

import android.R.attr.duration
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appruido.data.AudioRepository
import com.example.appruido.data.CriticalNoise
import com.example.appruido.data.CriticalNoiseRepository
import com.example.appruido.data.HistoricoEntity
import com.example.appruido.data.HistoricoRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.absoluteValue


class HomeScreenViewModel(
    val audioRepository: AudioRepository, val criticalNoiseRepository: CriticalNoiseRepository,
    private val historicoRepository: HistoricoRepository
) : ViewModel() {
    

    //Para armazenar valor de db em 1 minuto:
    private val _decibelsList = mutableListOf<Double>()
    private var _lastInsertTime = System.currentTimeMillis()
    private val ONE_MINUTE_IN_MILLIS = 60000L // 60 segundos * 1000 ms/s

    private val TAG: String = "HomeScreenViewModel"

    val userId = Firebase.auth.currentUser?.uid ?: ""

    @OptIn(FlowPreview::class)
    val decibels: StateFlow<Double> = audioRepository.decibels.sample(333).stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = 0.0
    )

    private val _history = MutableStateFlow<List<Double>>(emptyList())
    val history: StateFlow<List<Double>> = _history



    private val criticalNoiseState = CriticalNoiseState()


    init {
        // Every time decibels emits, update the history buffer
        viewModelScope.launch {
            val criticalHistory = criticalNoiseRepository.getAll(userId = userId).map { it.average }
            Log.d(TAG, "criticalHistory: $criticalHistory")

            decibels.collect { value ->
                var dbValue = value.absoluteValue
                if (dbValue.isInfinite()) {
                    dbValue = 0.0
                }

                // Update history for the chart
                val updated = _history.value.plus(dbValue).takeLast(25)
                _history.value = updated

                handleNoiseEvent(dbValue)

                // Lógica para armazenar média dos db a cada 1 minuto:
                _decibelsList.add(dbValue)
                val currentTime = System.currentTimeMillis()
                if (currentTime - _lastInsertTime >= ONE_MINUTE_IN_MILLIS) {
                    // Calcula média:
                    val averageDb = if (_decibelsList.isNotEmpty()) {
                        _decibelsList.average().toFloat()
                    } else {
                        0f
                    }
                    // Salva no BD interno de historico:
                    if (averageDb > 0) {
                        insertHistorico(averageDb, _lastInsertTime)
                    }
                    _decibelsList.clear()//Reseta lista
                    _lastInsertTime = currentTime
                    Log.d(TAG, "Histórico inserido. Média DB: $averageDb")
                }
            }
        }
    }

    private val _isRunning = MutableStateFlow(audioRepository.isRecording)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun setIsRunning(value: Boolean) {
        _isRunning.value = value
    }

    private fun sendAudioCriticalEvent(average: Double, startedAt: Date) {
        viewModelScope.launch {
            val audioCritical = CriticalNoise(
                average = average, startedAt = startedAt, endedAt = Date(), // Event ends now
                userId = Firebase.auth.currentUser?.uid ?: ""
            )
            criticalNoiseRepository.insert(audioCritical)
        }
    }

    private fun handleNoiseEvent(dbValue: Double) {
        if (dbValue >= 80) {
            Log.d(TAG, "noise enter critical: dbValue: $dbValue")

            criticalNoiseState.update(dbValue)
        } else {
            // Nível de barulho caiu.
            if (!criticalNoiseState.isCounting()) {
                return
            }

            val maxDb =criticalNoiseState.getMaxDb()
            val startedAt = criticalNoiseState.getStartedAt()!!


            Log.d(TAG, "noise leave critical: dbValue: $dbValue, duration: $duration")

            if (criticalNoiseState.ellapsedTime() >= 3000 || maxDb >= 100) {
                sendAudioCriticalEvent(maxDb, startedAt)
            }

            criticalNoiseState.reset()
        }
    }
    //Função para inserir dados historico:
    private fun insertHistorico(averageDecibels: Float, timestamp: Long) {

        val tipo = when {
            averageDecibels <= 35f -> 1f          // Baixo
            averageDecibels <= 65f -> 2f          // Moderado
            averageDecibels <= 100f -> 3f         // Perigo
            else -> 4f                            // Extremo  Perigo
        }

        val historico = HistoricoEntity(
            decibeis = averageDecibels,
            dataHora = timestamp,
            tipo = tipo
        )
        viewModelScope.launch {
            try {
                historicoRepository.insertHistorico(historico)
                Log.d(TAG, "HistoricoEntity inserido: $historico")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao inserir HistoricoEntity: ${e.message}")
            }
        }
    }

}

private class CriticalNoiseState {
    private var maxDb: Double = 0.0
    private var startedAt: Date? = null
    private var startTime: Long? = null

    fun update(dbValue: Double) {
        if (startTime == null) {
            maxDb = dbValue
            startedAt = Date()
            startTime = System.currentTimeMillis()
        } else {
            if (dbValue > maxDb) {
                maxDb = dbValue
            }
        }
    }

    fun isCounting(): Boolean {
        return startTime != null
    }

    fun getStartedAt(): Date? {
        return startedAt
    }

    fun getMaxDb(): Double {
        return maxDb
    }

    fun ellapsedTime(): Long {
        val currentTime = System.currentTimeMillis()
        return startTime?.let { currentTime - it } ?: 0
    }


    fun reset() {
        maxDb = 0.0
        startedAt = null
        startTime = null
    }



}