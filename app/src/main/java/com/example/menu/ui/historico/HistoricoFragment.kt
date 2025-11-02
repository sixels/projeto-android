package com.example.menu.ui.historico

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.menu.R
import com.example.menu.databinding.FragmentHistoricoBinding

class HistoricoFragment : Fragment() {

    // Referência para o TextView, se estiver usando View Binding, use-o
    private var _binding: FragmentHistoricoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Supondo que você esteja usando View Binding
        _binding = FragmentHistoricoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Encontra o TextView que será clicado
        val textViewPeriodo = binding.textPeriodo// Usando View Binding (ou findViewById(R.id.text_historico))

        // 2. Adiciona o listener de clique
        textViewPeriodo.setOnClickListener {
            // 3. Chama a função que exibe o menu
            showPeriodoPopupMenu(it)
        }
    }

    /**
     * Função para mostrar o PopupMenu e lidar com as seleções.
     * @param view A View que serve de âncora para o menu (o TextView no nosso caso).
     */
    private fun showPeriodoPopupMenu(view: View) {
        val popup = PopupMenu(context, view)
        popup.menuInflater.inflate(R.menu.menu_periodo, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_hoje -> {
                    (view as TextView).text = "Hoje" // Atualiza o texto
                    // Lógica: recarregar os dados para "Hoje"
                    true
                }
                R.id.action_ultimos_7_dias -> {
                    (view as TextView).text = "Últimos 7 Dias" // Atualiza o texto
                    // Lógica: recarregar os dados para "7 Dias"
                    true
                }
                R.id.action_mes_atual -> {
                    (view as TextView).text = "Mês Atual" // Atualiza o texto
                    // Lógica: recarregar os dados para "Mês Atual"
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}