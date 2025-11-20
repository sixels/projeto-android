package com.example.appruido.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.appruido.ui.navigation.Screen
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoricoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(historico: HistoricoEntity)
    @Delete
    suspend fun delete(historico: HistoricoEntity)
    // --- CONSULTA PARA GRÁFICO DE PIZZA (Contagem por Tipo) ---

    /**
     * Retorna a contagem de medições para cada tipo (1, 2, 3, 4) dentro de um período.
     * O resultado deve ser mapeado para a data class TipoContagem.
     *
     * @param dataInicialMilisegundos: O timestamp UNIX do limite inferior do período.
     */
    @Query("""
        SELECT 
            tipo, 
            COUNT(tipo) as contagem 
        FROM 
            historico 
        WHERE 
            dataHora >= :dataInicialMilisegundos 
        GROUP BY 
            tipo
    """)
    fun getContagemPorTipo(dataInicialMilisegundos: Long): Flow<List<TipoContagem>>
}
