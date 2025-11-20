package com.example.appruido.ui.screens
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.bytebeats.views.charts.pie.PieChartData

class HistoricoScreenViewModel : ViewModel() {
    private val _dadosGrafico = MutableStateFlow(
        listOf(
            PieChartData.Slice(50f, cor1Azul),
            PieChartData.Slice(35f, cor2Amarelo),
            PieChartData.Slice(15f, cor3Laranja),
            PieChartData.Slice(5f, cor4Vermelho)
        )
    )
    val dadosGrafico: StateFlow<List<PieChartData.Slice>> = _dadosGrafico
    private val _periodoSelecionado = MutableStateFlow("")
    val periodoSelecionado: StateFlow<String> get() = _periodoSelecionado
    fun selecionarPeriodo(periodo: String) {
        _periodoSelecionado.value = periodo
    }


}

