package com.example.appruido

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appruido.ui.components.MenuScaffold
import com.example.appruido.ui.navigation.AppNavHost
import com.example.appruido.ui.navigation.Screen
import com.example.appruido.ui.theme.AppRuidoTheme

class MainActivity : ComponentActivity() {



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AppRuidoTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

                val currentScreenTitle = when (currentRoute) {
                    Screen.Home.route -> "Tela inicial"
                    Screen.Settings.route -> "Configurações"
                    Screen.Historico.route -> "Histórico"
                    else -> "App Ruido"
                }


                MenuScaffold(
                    navController = navController,
                    currentRoute = currentRoute,
                    currentScreenTitle = currentScreenTitle,
                    onSignOutClicked = {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                        startActivity(intent)
                        finish()
                    },
                ) { innerPadding ->
                    AppNavHost(
                        activity = this,
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun ComponentActivity.hasAudioPermission() =
    checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED

fun ComponentActivity.requestAudioPermission() {
    requestPermissions(
        arrayOf(android.Manifest.permission.RECORD_AUDIO),
        1001
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ComponentActivity.startAudioService() {
    val intent = Intent(this, AudioService::class.java)
    startForegroundService(intent)
}

fun ComponentActivity.stopAudioService() {
    val intent = Intent(this, AudioService::class.java)
    stopService(intent)
}
