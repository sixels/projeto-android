package com.example.appruido.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appruido.data.CriticalNoise
import com.example.appruido.data.CriticalNoiseRepository
import com.example.appruido.data.HistoricoEntity
import com.example.appruido.data.HistoricoRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.bytebeats.views.charts.pie.PieChartData

data class NoiseAnalysisResult(
    val averageDb: Double = 0.0,
    val eventCount: Int = 0,
    val levels: Map<String, LevelStats> = emptyMap()
)

data class LevelStats(
    val eventCount: Int = 0,
    val averageDurationMillis: Double = 0.0
)

class HistoricoScreenViewModel(
    private val historicoRepository: HistoricoRepository,
    private val criticalNoiseRepository: CriticalNoiseRepository
) : ViewModel() {

    val listaHistorico: StateFlow<List<HistoricoEntity>> = historicoRepository.getAllHistorico()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _criticalNoises = MutableStateFlow<List<CriticalNoise>>(emptyList())

    private val _dataInicioGrafico = MutableStateFlow(System.currentTimeMillis() - 86400000L)
    private val _periodoSelecionadoTexto = MutableStateFlow("24h")
    val periodoSelecionado: StateFlow<String> = _periodoSelecionadoTexto

    val noiseAnalysis: StateFlow<NoiseAnalysisResult> = combine(
        _criticalNoises, _dataInicioGrafico
    ) { noises, dataInicio ->
        val filteredNoises = noises.filter { it.startedAt?.time ?: 0 >= dataInicio }
        calculateAnalysis(filteredNoises)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NoiseAnalysisResult()
    )

    init {
        loadCriticalNoises()
    }

    private fun loadCriticalNoises() {
        viewModelScope.launch {
            val userId = Firebase.auth.currentUser?.uid
            if (userId != null) {
                _criticalNoises.value = criticalNoiseRepository.getAll(userId)
            }
        }
    }

    private fun calculateAnalysis(noises: List<CriticalNoise>): NoiseAnalysisResult {
        if (noises.isEmpty()) {
            return NoiseAnalysisResult()
        }

        val averageDb = noises.map { it.average }.average()
        val eventCount = noises.size

        val levels = mapOf(
            "80 a 85dB" to (80.0 to 85.0),
            "85 a 88dB" to (85.0 to 88.0),
            "88 a 91dB" to (88.0 to 91.0),
            "91 a 94dB" to (91.0 to 94.0),
            "94 a 97dB" to (94.0 to 97.0),
            "97 a 100dB" to (97.0 to 100.0),
            "acima de 100dB" to (100.0 to Double.MAX_VALUE)
        ).mapValues { (_, range) ->
            val matchingNoises = noises.filter { it.average >= range.first && it.average < range.second }
            val duration = if (matchingNoises.isNotEmpty()) {
                matchingNoises.mapNotNull { it.endedAt?.time?.minus(it.startedAt?.time ?: 0) }.average()
            } else 0.0

            LevelStats(
                eventCount = matchingNoises.size,
                averageDurationMillis = duration
            )
        }

        return NoiseAnalysisResult(
            averageDb = averageDb,
            eventCount = eventCount,
            levels = levels
        )
    }

    fun selecionarPeriodo(periodo: String) {
        _periodoSelecionadoTexto.value = periodo
        val agora = System.currentTimeMillis()
        _dataInicioGrafico.value = when (periodo) {
            "24h" -> agora - (24 * 60 * 60 * 1000L)
            "7d" -> agora - (7 * 24 * 60 * 60 * 1000L)
            "30d" -> agora - (30 * 24 * 60 * 60 * 1000L)
            else -> agora - (24 * 60 * 60 * 1000L)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val dadosGrafico: StateFlow<List<PieChartData.Slice>> = _dataInicioGrafico.flatMapLatest { dataInicio ->
        historicoRepository.getContagemPorTipo(dataInicio)
    }.map { listaContagem ->
        if (listaContagem.isEmpty()) {
            listOf(PieChartData.Slice(1f, Color.LightGray))
        } else {
            listaContagem.map { item ->
                val cor = when (item.tipo) {
                    1f -> Color(0xFF4CAF50)
                    2f -> Color(0xFFFFC107)
                    3f -> Color(0xFFFF9800)
                    4f -> Color(0xFFF44336)
                    else -> Color.Gray
                }
                PieChartData.Slice(item.contagem.toFloat(), cor)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(PieChartData.Slice(1f, Color.LightGray))
    )
}

class HistoricoViewModelFactory(
    private val historicoRepository: HistoricoRepository,
    private val criticalNoiseRepository: CriticalNoiseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricoScreenViewModel(historicoRepository, criticalNoiseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
