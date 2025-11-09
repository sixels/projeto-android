package com.example.appruido.ui.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appruido.hasAudioPermission
import com.example.appruido.requestAudioPermission
import com.example.appruido.startAudioService
import com.example.appruido.stopAudioService

import com.github.yamin8000.gauge.main.Gauge
import com.github.yamin8000.gauge.main.GaugeNumerics
import com.github.yamin8000.gauge.ui.style.GaugeArcStyle
import com.github.yamin8000.gauge.ui.style.GaugeStyle
import java.text.DecimalFormat


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(
    activity: ComponentActivity,
    viewModel: HomeScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel(), modifier: Modifier = Modifier) {
    val db by viewModel.decibels.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GaugeMeasure(db)
        Button(
            onClick = {
                if (!isRunning) {
                    if (!activity.hasAudioPermission()) {
                        activity.requestAudioPermission()
                    } else {
                        activity.startAudioService()
                        viewModel.setIsRunning(true)
                    }
                } else {
                    activity.stopAudioService()
                    viewModel.setIsRunning(false)
                }
            }
        ) {
            Text(if (isRunning) "Pausar Medição" else "Iniciar Medição")
        }
    }
}



@Composable
@Preview(widthDp = 300, showBackground = true)
fun GaugeMeasure(measure: Double = 0.0) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
            GaugeMeter(measure)
        }
    }
}

@Composable
fun GaugeMeter(measure: Double) {
    val animatedValue by androidx.compose.animation.core.animateFloatAsState(
        targetValue = measure.toFloat(),
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 300,      // tune for smoother/slower animation
            easing = androidx.compose.animation.core.LinearOutSlowInEasing
        ),
        label = "gaugeAnimation"
    )


    Surface {
            Gauge(
                modifier = Modifier.padding(8.dp),
                value = animatedValue,
                decimalFormat = DecimalFormat().apply { maximumFractionDigits = 0 },
                numerics = GaugeNumerics(
                    startAngle = 150,
                    sweepAngle = 240,
                    valueRange = 0f..120f,
                    smallTicksStep = 2,
                    bigTicksStep = 20
                ),
                valueUnit = "dB",
                style = GaugeStyle(
                    hasBorder = false,
                    arcStyle = GaugeArcStyle(
                        hasArcs = true,
                        hasProgressiveAlpha = false,
                        bigTicksHasLabels = true,
                        cap = StrokeCap.Round
                    ),
                ),
            )
        }
}