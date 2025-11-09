package com.example.appruido.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

import androidx.compose.runtime.*


@Composable
fun HistoricoScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Página histórico")
    }
    dropdown_menu() //caixa de opções de periodo de tempo


}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dropdown_menu(){

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
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = "Periodo 1")},
                onClick = {
                    periodo = "Periodo 1"
                    isExpanded = false
                }
                
            )
            DropdownMenuItem(
                text = {
                    Text(text = "Periodo 2")},
                onClick = {
                    periodo = "Periodo 2"
                    isExpanded = false
                }

            )
        }
    }
}
