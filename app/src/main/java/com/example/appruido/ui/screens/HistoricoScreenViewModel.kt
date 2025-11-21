package com.example.appruido.ui.screens
import androidx.lifecycle.ViewModel
import com.example.appruido.data.HistoricoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.bytebeats.views.charts.pie.PieChartData

class HistoricoScreenViewModel(historicoRepository: HistoricoRepository) : ViewModel() {
    //Precisa fazer o dadosGrafico receber os dados do banco do Historico:
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

