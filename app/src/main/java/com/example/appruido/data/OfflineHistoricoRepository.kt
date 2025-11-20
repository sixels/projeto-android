package com.example.appruido.data

import kotlinx.coroutines.flow.Flow


class OfflineHistoricoRepository(private val HistoricoDao: HistoricoDao) : HistoricoRepository {

    override fun getContagemPorTipo(dataInicialMilisegundos: Long): Flow<List<TipoContagem>> =
        HistoricoDao.getContagemPorTipo(dataInicialMilisegundos)

    override suspend fun insertHistorico(historico: HistoricoEntity) =
        HistoricoDao.insert(historico)

    override suspend fun deleteHistorico(historico: HistoricoEntity) =
        HistoricoDao.delete(historico)


}