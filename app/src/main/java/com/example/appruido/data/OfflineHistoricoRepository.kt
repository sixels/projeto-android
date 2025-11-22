package com.example.appruido.data

import kotlinx.coroutines.flow.Flow


class OfflineHistoricoRepository(private val historicoDao: HistoricoDao) : HistoricoRepository {

    override fun getContagemPorTipo(dataInicialMilisegundos: Long): Flow<List<TipoContagem>> =
        historicoDao.getContagemPorTipo(dataInicialMilisegundos)

    override suspend fun insertHistorico(historico: HistoricoEntity) =
        historicoDao.insert(historico)

    override suspend fun deleteHistorico(historico: HistoricoEntity) =
        historicoDao.delete(historico)

    override fun getAllHistorico(): Flow<List<HistoricoEntity>> {
        return historicoDao.getAllHistorico()
    }
}