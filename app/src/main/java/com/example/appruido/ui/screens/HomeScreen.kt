package com.example.appruido.ui.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appruido.hasAudioPermission
import com.example.appruido.requestAudioPermission
import com.example.appruido.startAudioService
import com.example.appruido.stopAudioService
import com.example.appruido.ui.components.Gauge
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import kotlin.math.absoluteValue


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    activity: ComponentActivity,
    viewModel: HomeScreenViewModel = viewModel(),
) {
    val db by viewModel.decibels.collectAsState()
    val history = viewModel.history.collectAsStateWithLifecycle().value
    val isRunning by viewModel.isRunning.collectAsState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val foreColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier,
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
        LineChart(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 22.dp),

            data = remember(history) {
                listOf(
                    Line(
                        label = "Decibels",
                        values = history,
                        color = SolidColor(primaryColor),
                        strokeAnimationSpec = tween(0),
                        firstGradientFillColor = primaryColor.copy(alpha = 0.5f),
                        gradientAnimationDelay = 0,
                        gradientAnimationSpec = tween(0),
                        secondGradientFillColor = Color.Transparent,
                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                    )
                )
            },
            animationDelay = 0,
            minValue = 0.0,
            maxValue = 120.0,
            animationMode = AnimationMode.Together(delayBuilder = {
                0
            }),
            labelHelperProperties = LabelHelperProperties(
                enabled = false,
            ),
            labelProperties = LabelProperties(enabled = false),
            indicatorProperties = HorizontalIndicatorProperties(
                enabled = true,
                textStyle = TextStyle(
                    color = foreColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
//                count = TODO(),
//                position = TODO(),
//                padding = TODO(),
//                contentBuilder = TODO(),
//                indicators = TODO(),
            ),
            dividerProperties = DividerProperties(enabled = false),
//            gridProperties = GridProperties(enabled = false),
            popupProperties = PopupProperties(enabled = false),
//            labelHelperPadding = TODO(),
//            textMeasurer = TODO(),
//            popupProperties = TODO(),
//            dotsProperties = TODO(),
        )
    }
}


@Composable
@Preview(widthDp = 300, showBackground = true)
fun GaugeMeasure(measure: Double = 0.0) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            GaugeMeter(measure)
        }
    }
}

@Composable
fun GaugeMeter(measure: Double) {
    var mMeasure = measure.absoluteValue
    if (mMeasure.isInfinite()) {
        mMeasure = 0.0
    }
    val animatedValue by animateFloatAsState(
        targetValue = mMeasure.toFloat(),
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        ),
        label = "gaugeAnimation"
    )

    val label = when {
        animatedValue >= 100 -> "Extremo";
        animatedValue >= 65 -> "Perigo";
        animatedValue >= 35 -> "Moderado";
        animatedValue > 0 -> "Baixo";
        else -> "";
    }


    Surface {
        Gauge(
            modifier = Modifier.padding(28.dp),
            maxValue = 120f,
            inputValue = animatedValue,
            progressColors = listOf(Color.Green, Color.Yellow, Color.Yellow,
                Color.Red),
            label = label,
        )
    }
}