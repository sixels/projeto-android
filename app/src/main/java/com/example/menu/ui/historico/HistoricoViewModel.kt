package com.example.menu.ui.historico

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoricoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tela de histórico"
    }
    val text: LiveData<String> = _text
}