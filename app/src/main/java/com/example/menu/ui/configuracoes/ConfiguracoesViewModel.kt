package com.example.menu.ui.configuracoes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfiguracoesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Aqui ficará as configurações"
    }
    val text: LiveData<String> = _text
}