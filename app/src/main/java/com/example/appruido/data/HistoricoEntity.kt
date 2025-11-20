package com.example.appruido.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historico")
data class HistoricoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val decibeis: Float,
    val dataHora: Long, //O long armazena um timestamp em millisegundos
    val tipo: Float //armazena valor de gravidade
    /*
    1f- Baixo
    2f-Moderado
    3f- Perigo
    4f-Extremo Perigo
     */
)