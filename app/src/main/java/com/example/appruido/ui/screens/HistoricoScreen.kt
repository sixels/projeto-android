package com.example.appruido.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appruido.AppViewModelProvider
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData


val cor1Azul = Color(0xFF2196F3)
val cor2Amarelo = Color(0xFFFFD54F)
val cor3Laranja = Color(0xFFFFA14F)
val cor4Vermelho = Color(0xFFF45559)
@Composable
fun HistoricoScreen(
    viewModel: HistoricoScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val dadosGraficos by viewModel.dadosGrafico.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Padding lateral para a tela
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text(text = "Selecione o período que deseja:", fontSize = 18.sp)
                Dropdown_menu() //caixa opções periodo
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Grafico e info historico:
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ---  LISTA ROLÁVEL:  ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(size = 15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Grafico(dadosGraficos)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.Start
                ){
                    Column(

                    ) {
                        Text("Intervalos de níveis de decibeis:")
                        Text("Baixo: 0db - 35db", color = cor1Azul)
                        Text("Moderado: 35db - 65db", color = cor2Amarelo)
                        Text("Perigo: 65dp - 100db ", color = cor3Laranja)
                        Text("Extremo perigo: 100db - 120db ", color = cor4Vermelho)
                    }

                }
            }
            item {//Card para mostrar dados do bd interno:
                ResumoCard {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Valor máximo: ")
                            }
                            append("aqui insira valor maximo do periodo")
                            append("\n")

                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Valor minimo: ")
                            }
                        }
                    )
                }
            }
            item { //Card para mostrar dados vindo do bd do firebase
                ResumoCard {
                    Text(text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Media de valores criticos e tempo: ")
                        }
                        append("Apresenta perigo caso essa media e esse valores estejam no intervalo da tabela de perigo")
                    }
                    )
                }
            }

        }
    }
}

@Composable
fun Grafico(dados: List<PieChartData.Slice>) {
    PieChart(
        pieChartData = PieChartData(
            slices = dados
        )
    )
}


@Composable
fun ResumoCard(conteudo: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp) // Padding dentro do card
        ) {
            conteudo()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown_menu(viewModel: HistoricoScreenViewModel = viewModel()){

    var isExpanded by remember {
        mutableStateOf(value = false)
    }

    val periodo by viewModel.periodoSelecionado.collectAsState()

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        TextField(
            value = periodo,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .width(180.dp)

        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = "Hoje")},
                onClick = {
                    viewModel.selecionarPeriodo("Hoje")
                    isExpanded = false
                }

            )
            DropdownMenuItem(
                text = {
                    Text(text = "Últimos 7 dias")},
                onClick = {
                    viewModel.selecionarPeriodo("Últimos 7 dias")
                    isExpanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Último mês")},
                onClick = {
                    viewModel.selecionarPeriodo( "Último mês" )
                    isExpanded = false
                }
            )
        }
    }
}

