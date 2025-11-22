package com.example.appruido.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appruido.ApplicationEntrypoint // <--- IMPORTANTE: O import da sua classe
import com.example.appruido.data.HistoricoEntity
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoricoScreen() {
    val context = LocalContext.current

    // --- A CORREÇÃO DO BANCO DE DADOS ---
    // Aqui pegamos a instância do aplicativo que já está rodando
    val application = context.applicationContext as ApplicationEntrypoint

    // E pegamos o repositório DE LÁ (o mesmo que a Home usa)
    val repositorio = application.container.historicoRepository

    // Injeta esse repositório "compartilhado" no ViewModel
    val viewModel: HistoricoScreenViewModel = viewModel(
        factory = HistoricoViewModelFactory(repositorio)
    )

    // Coleta os dados
    val listaHistorico by viewModel.listaHistorico.collectAsState()
    val dadosGrafico by viewModel.dadosGrafico.collectAsState()
    val periodoSelecionado by viewModel.periodoSelecionado.collectAsState()

    // --- O Visual (Gráfico + Lista) ---
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
            // 1. O CARTÃO DO GRÁFICO
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Cabeçalho (Título + Dropdown)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Distribuição", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            MenuPeriodo(periodoSelecionado) { viewModel.selecionarPeriodo(it) }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Desenho do Gráfico
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

            // 2. TÍTULO DA LISTA
            item {
                Text(
                    "Registros Recentes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // 3. A LISTA DE DADOS
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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