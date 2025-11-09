package com.example.appruido.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Historico : Screen("historico")
    object Settings : Screen("settings")
}
