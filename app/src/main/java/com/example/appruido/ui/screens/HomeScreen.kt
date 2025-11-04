package com.example.appruido.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.github.yamin8000.gauge.main.Gauge
import com.github.yamin8000.gauge.main.GaugeNumerics
import com.github.yamin8000.gauge.ui.style.GaugeArcStyle
import com.github.yamin8000.gauge.ui.style.GaugeStyle


@Composable
fun Home(name: String, modifier: Modifier = Modifier) {
    Column() {
        GaugeMeasure()
    }
}

@Composable
@Preview(widthDp = 300, showBackground = true)
fun GaugeMeasure() {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
            GaugeMeter()
        }
    }
}

@Composable
fun GaugeMeter() {
        Surface {
            Gauge(
                modifier = Modifier.padding(8.dp),
                value = 22f,
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