package com.example.appruido.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import com.example.appruido.R


import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun HistoricoScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier,
                 verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text( text = "Selecione o período que deseja:", fontSize = 18.sp)
                Dropdown_menu() //caixa de opções de periodo de tempo

            }
        }
        Grafico()



    }




}

@Composable
 fun Grafico(){

    val imageId = R.drawable.grafico_img
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .clip(RoundedCornerShape(size = 15.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "Fundo do Gráfico",
            modifier = Modifier.fillMaxSize(), // Para preencher o Box
            contentScale = ContentScale.Fit // Para ajustar e cortar
        )

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown_menu(){

    var isExpanded by remember {
        mutableStateOf(value = false)
    }

    var periodo by remember {
        mutableStateOf(value = "")
    }

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
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .width(170.dp)

        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = "Hoje")},
                onClick = {
                    periodo = "Hoje"
                    isExpanded = false
                }
                
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Últimos 7 dias")},
                onClick = {
                    periodo = "Últimos 7 dias"
                    isExpanded = false
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Último mês")},
                onClick = {
                    periodo = "Último mês"
                    isExpanded = false
                }
            )
        }
    }
}
