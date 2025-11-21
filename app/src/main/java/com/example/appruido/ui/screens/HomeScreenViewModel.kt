package com.example.appruido.ui.screens

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.absoluteValue

class HomeScreenViewModel(
    val audioRepository: AudioRepository, // Tornando público
    private val criticalNoiseRepository: CriticalNoiseRepository,
    private val historicoRepository: HistoricoRepository
) : ViewModel() {

    @OptIn(FlowPreview::class)
    val decibels: StateFlow<Double> =
        audioRepository.decibels
            .sample(333)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )

    private val _history = MutableStateFlow<List<Double>>(emptyList())
    val history: StateFlow<List<Double>> = _history.asStateFlow()

    private val TAG = "HomeScreenViewModel"

    //Para armazenar valor de db em 1 minuto:
    private val _decibelsList = mutableListOf<Double>()
    private var _lastInsertTime = System.currentTimeMillis()
    private val ONE_MINUTE_IN_MILLIS = 60000L // 60 segundos * 1000 ms/s


    // O estado de gravação agora vem DIRETAMENTE do repositório
    val isRunning: StateFlow<Boolean> = audioRepository.isRecording

    // State for high noise detection
    private var highNoiseEventStartTime: Long? = null
    private var highNoiseEventMaxDb: Double = 0.0
    private var highNoiseEventStartedAt: Date? = null

    init {
        viewModelScope.launch {
            decibels.collect { value ->
                var dbValue = value.absoluteValue
                if (dbValue.isInfinite() || dbValue.isNaN()) {
                    dbValue = 0.0
                }

                val updated = _history.value
                    .plus(dbValue)
                    .takeLast(25)
                _history.value = updated

                // Só processa eventos de ruído se a medição estiver ativa
                if (isRunning.value) {
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
    }

    private fun handleNoiseEvent(dbValue: Double) {
        if (dbValue >= 80) {
            if (highNoiseEventStartTime == null) {
                highNoiseEventStartTime = System.currentTimeMillis()
                highNoiseEventMaxDb = dbValue
                highNoiseEventStartedAt = Date()
            } else {
                if (dbValue > highNoiseEventMaxDb) {
                    highNoiseEventMaxDb = dbValue
                }
            }
        } else { // dbValue < 80
            highNoiseEventStartTime?.let { startTime ->
                val duration = System.currentTimeMillis() - startTime
                if (duration > 3000) {
                    sendAudioCriticalEvent(highNoiseEventMaxDb, highNoiseEventStartedAt!!)
                }
            }
            highNoiseEventStartTime = null
            highNoiseEventMaxDb = 0.0
            highNoiseEventStartedAt = null
        }
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