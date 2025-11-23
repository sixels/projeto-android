package com.example.appruido.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appruido.ApplicationEntrypoint
import com.example.appruido.data.HistoricoEntity
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun HistoricoScreen() {
    val context = LocalContext.current

    val application = context.applicationContext as ApplicationEntrypoint
    val historicoRepository = application.container.historicoRepository
    val criticalNoiseRepository = application.container.criticalNoiseRepository

    val viewModel: HistoricoScreenViewModel = viewModel(
        factory = HistoricoViewModelFactory(historicoRepository, criticalNoiseRepository)
    )

    val listaHistorico by viewModel.listaHistorico.collectAsState()
    val dadosGrafico by viewModel.dadosGrafico.collectAsState()
    val periodoSelecionado by viewModel.periodoSelecionado.collectAsState()
    val analysis by viewModel.noiseAnalysis.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Histórico de Ruído",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Distribuição", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            MenuPeriodo(periodoSelecionado) { viewModel.selecionarPeriodo(it) }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        if (dadosGrafico.isNotEmpty()) {
                            PieChart(
                                pieChartData = PieChartData(slices = dadosGrafico),
                                modifier = Modifier.size(200.dp),
                                sliceDrawer = SimpleSliceDrawer(sliceThickness = 25f)
                            )
                        } else {
                            Text("Sem dados neste período", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Análise de Ruídos Críticos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (analysis.eventCount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Média Geral", fontWeight = FontWeight.SemiBold)
                                    Text(String.format("%.1f dB", analysis.averageDb), fontSize = 20.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Total Eventos", fontWeight = FontWeight.SemiBold)
                                    Text(analysis.eventCount.toString(), fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            analysis.levels.forEach { (level, stats) ->
                                if (stats.eventCount > 0) {
                                    AnalysisDetailRow(level, stats)
                                }
                            }
                        } else {
                            Text("Nenhum evento crítico no período.", color = Color.Gray)
                        }
                    }
                }
            }

            item {
                Text(
                    "Registros Recentes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (listaHistorico.isEmpty()) {
                item {
                    Text("Nenhum dado encontrado.", color = Color.Gray)
                }
            } else {
                items(listaHistorico) { item ->
                    ItemHistorico(historico = item)
                }
            }
        }
    }
}

@Composable
private fun AnalysisDetailRow(level: String, stats: LevelStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = level, fontWeight = FontWeight.Medium)
        Text(text = "${formatDuration(stats.averageDurationMillis.toLong())} / ${stats.eventCount} eventos", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatDuration(millis: Long): String {
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
    return String.format("%d seg", seconds)
}

@Composable
fun MenuPeriodo(periodoAtual: String, onPeriodoChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val opcoes = listOf("24h", "7d", "30d")

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when(periodoAtual) {
                    "24h" -> "Últimas 24h"
                    "7d" -> "7 Dias"
                    "30d" -> "30 Dias"
                    else -> periodoAtual
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.padding(start = 4.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = {
                        Text(when(opcao) {
                            "24h" -> "Últimas 24h"
                            "7d" -> "Últimos 7 Dias"
                            "30d" -> "Últimos 30 Dias"
                            else -> opcao
                        })
                    },
                    onClick = { onPeriodoChange(opcao); expanded = false }
                )
            }
        }
    }
}

@Composable
fun ItemHistorico(historico: HistoricoEntity) {
    val (cor, textoGravidade) = when (historico.tipo) {
        1f -> Color(0xFF4CAF50) to "Baixo"
        2f -> Color(0xFFFFC107) to "Moderado"
        3f -> Color(0xFFFF9800) to "Perigo"
        4f -> Color(0xFFF44336) to "Extremo"
        else -> Color.Gray to "Desconhecido"
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = String.format("%.1f dB", historico.decibeis),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatarData(historico.dataHora),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(color = cor, shape = MaterialTheme.shapes.small) {
                Text(
                    text = textoGravidade,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatarData(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
