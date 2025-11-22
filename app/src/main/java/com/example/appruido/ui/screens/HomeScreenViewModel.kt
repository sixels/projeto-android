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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.math.absoluteValue

class HomeScreenViewModel(
    val audioRepository: AudioRepository,
    private val criticalNoiseRepository: CriticalNoiseRepository,
    private val historicoRepository: HistoricoRepository
) : ViewModel() {

    private val TAG = "DEBUG_RUIDO" // Tag nova para facilitar o filtro

    @OptIn(FlowPreview::class)
    val decibels: StateFlow<Double> =
        audioRepository.decibels
            .sample(100) // Diminuí para pegar mais amostras e não perder o finalzinho
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )

    private val _history = MutableStateFlow<List<Double>>(emptyList())
    val history: StateFlow<List<Double>> = _history.asStateFlow()

    // Estado da gravação
    val isRunning: StateFlow<Boolean> = audioRepository.isRecording

    // Lista temporária
    private val _decibelsList = mutableListOf<Double>()
    private var _lastInsertTime = System.currentTimeMillis()

    // Tempo para salvar automático (1 minuto padrão)
    private val SAVE_INTERVAL_MILLIS = 60000L // 60s * 1000ms
    // Variáveis de pico de ruído
    private var highNoiseEventStartTime: Long? = null
    private var highNoiseEventMaxDb: Double = 0.0
    private var highNoiseEventStartedAt: Date? = null

    init {
        // 1. COLETA DE DADOS
        viewModelScope.launch {
            decibels.collect { value ->
                var dbValue = value.absoluteValue
                if (dbValue.isInfinite() || dbValue.isNaN()) dbValue = 0.0

                // Atualiza visual
                _history.value = _history.value.plus(dbValue).takeLast(25)

                // Se estiver gravando, guarda na lista
                if (isRunning.value) {
                    handleNoiseEvent(dbValue)
                    _decibelsList.add(dbValue)

                    // Salvamento automático por tempo
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - _lastInsertTime >= SAVE_INTERVAL_MILLIS) {
                        Log.e(TAG, "Tempo limite atingido. Salvando parcial...")
                        salvarBuffer(currentTime)
                    }
                }
            }
        }

        // Detector de parada
        viewModelScope.launch {
            isRunning.collect { gravando ->
                if (!gravando) {
                    // O usuário parou a gravação.
                    Log.e(TAG, "Botão Parar detectado. Dados na memória: ${_decibelsList.size}")

                    if (_decibelsList.isNotEmpty()) {
                        salvarBuffer(System.currentTimeMillis())
                    } else {
                        Log.e(TAG, "Lista vazia. Nada para salvar.")
                    }

                    // Reseta variáveis
                    highNoiseEventStartTime = null
                    highNoiseEventMaxDb = 0.0
                    highNoiseEventStartedAt = null
                } else {
                    // Começou a gravar
                    Log.e(TAG, "Iniciando gravação...")
                    _decibelsList.clear()
                    _lastInsertTime = System.currentTimeMillis()
                }
            }
        }
    }

    // Função blindada contra cancelamento de tela
    private fun salvarBuffer(timestamp: Long) {
        if (_decibelsList.isEmpty()) return

        val averageDb = _decibelsList.average().toFloat()

        // Limpa a lista IMEDIATAMENTE para evitar duplicidade
        _decibelsList.clear()
        _lastInsertTime = timestamp

        if (averageDb > 0) {
            val historico = criarEntidade(averageDb, timestamp)

            // LANÇA UMA COROUTINE QUE NÃO MORRE SE A TELA FECHAR
            viewModelScope.launch {
                // 'NonCancellable' garante que o banco termine de gravar
                withContext(NonCancellable) {
                    try {
                        historicoRepository.insertHistorico(historico)
                        Log.e(TAG, "SUCESSO! Salvo no banco: $averageDb dB")
                    } catch (e: Exception) {
                        Log.e(TAG, "ERRO CRÍTICO AO SALVAR: ${e.message}")
                    }
                }
            }
        }
    }

    private fun criarEntidade(averageDecibels: Float, timestamp: Long): HistoricoEntity {
        val tipo = when {
            averageDecibels <= 35f -> 1f
            averageDecibels <= 65f -> 2f
            averageDecibels <= 100f -> 3f
            else -> 4f
        }
        return HistoricoEntity(
            decibeis = averageDecibels,
            dataHora = timestamp,
            tipo = tipo
        )
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
        } else {
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
            try {
                val audioCritical = CriticalNoise(
                    average = average, startedAt = startedAt, endedAt = Date(),
                    userId = Firebase.auth.currentUser?.uid ?: ""
                )
                criticalNoiseRepository.insert(audioCritical)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao salvar Ruido Critico: ${e.message}")
            }
        }
    }

    private fun insertHistorico(averageDecibels: Float, timestamp: Long) {
    }
}