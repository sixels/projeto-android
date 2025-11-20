package com.example.appruido.data

data class TipoContagem(
    /**
     * Modelo de dados auxiliar usado para mapear o resultado da consulta SQL
     * que agrupa e conta as medições de decibéis por tipo de gravidade.
     */
    val tipo: Float,        // Corresponde ao campo 'tipo' agrupado (1, 2, 3 ou 4)
    val contagem: Float     // Corresponde ao alias 'contagem' da função COUNT(tipo)
)