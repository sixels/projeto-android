package com.example.appruido.ui.navigation

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appruido.ui.screens.HistoricoScreen
import com.example.appruido.ui.screens.Home
import com.example.appruido.ui.screens.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    activity: ComponentActivity,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, 
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(Screen.Home.route) {
            Home(activity = activity)
        }
        composable(Screen.Historico.route) {
            HistoricoScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
