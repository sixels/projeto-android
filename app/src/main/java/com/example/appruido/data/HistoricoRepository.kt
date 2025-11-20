package com.example.appruido.data

import kotlinx.coroutines.flow.Flow

interface HistoricoRepository {

    fun getContagemPorTipo(dataInicialMilisegundos: Long): Flow<List<TipoContagem>>

    suspend fun insertHistorico(historico: HistoricoEntity)

    suspend fun  deleteHistorico(historico: HistoricoEntity)


}