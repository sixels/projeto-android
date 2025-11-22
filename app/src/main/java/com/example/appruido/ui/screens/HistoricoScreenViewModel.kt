package com.example.appruido.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.appruido.data.HistoricoEntity
import com.example.appruido.data.HistoricoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.bytebeats.views.charts.pie.PieChartData

class HistoricoScreenViewModel(private val repository: HistoricoRepository) : ViewModel() {

    val listaHistorico: StateFlow<List<HistoricoEntity>> = repository.getAllHistorico()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _dataInicioGrafico = MutableStateFlow(System.currentTimeMillis() - 86400000L)
    private val _periodoSelecionadoTexto = MutableStateFlow("24h") // Começa com 24h
    val periodoSelecionado: StateFlow<String> = _periodoSelecionadoTexto

    fun selecionarPeriodo(periodo: String) {
        _periodoSelecionadoTexto.value = periodo
        val agora = System.currentTimeMillis()
        _dataInicioGrafico.value = when (periodo) {
            "24h" -> agora - (24 * 60 * 60 * 1000L)
            "7d"  -> agora - (7 * 24 * 60 * 60 * 1000L)
            "30d" -> agora - (30 * 24 * 60 * 60 * 1000L)
            else  -> agora - (24 * 60 * 60 * 1000L)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val dadosGrafico: StateFlow<List<PieChartData.Slice>> = _dataInicioGrafico.flatMapLatest { dataInicio ->
        repository.getContagemPorTipo(dataInicio)
    }.map { listaContagem ->
        if (listaContagem.isEmpty()) {
            // Retorna uma fatia cinza vazia se não tiver dados, para o gráfico não sumir
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

class HistoricoViewModelFactory(private val repository: HistoricoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricoScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricoScreenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}