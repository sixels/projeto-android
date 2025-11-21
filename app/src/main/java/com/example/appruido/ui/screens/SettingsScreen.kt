package com.example.appruido.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appruido.AppViewModelProvider
import com.example.appruido.hasAudioPermission
import com.example.appruido.requestAudioPermission

@Composable
fun SettingsScreen(
    activity: ComponentActivity,
    viewModel: SettingsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val calibrationState by viewModel.calibrationState.collectAsState()
    val calibrationValue by viewModel.calibrationValue.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Calibração do Microfone",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Para garantir medições precisas, calibre o microfone em um ambiente o mais silencioso possível. O ideal é um nível de ruído em torno de 33dB.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        when (calibrationState) {
            CalibrationState.IDLE -> {
                Button(onClick = {
                    if (!activity.hasAudioPermission()) {
                        activity.requestAudioPermission()
                    } else {
                        viewModel.startCalibration()
                    }
                }) {
                    Text("Iniciar Calibração")
                }
            }
            CalibrationState.CALIBRATING -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Calibrando... Mantenha o silêncio.")
            }
            CalibrationState.FINISHED -> {
                Text(
                    text = "Calibração Concluída!",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                calibrationValue?.let {
                    Text(text = "Ajuste aplicado: %.2f dB".format(it))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (!activity.hasAudioPermission()) {
                        activity.requestAudioPermission()
                    } else {
                        viewModel.startCalibration()
                    }
                }) {
                    Text("Recalibrar")
                }
            }
        }
    }
}