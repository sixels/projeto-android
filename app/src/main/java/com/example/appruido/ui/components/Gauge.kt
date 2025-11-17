package com.example.appruido.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

// Baseado no artigo: https://proandroiddev.com/creating-a-custom-gauge-speedometer-in-jetpack-compose-bd3c3d72074b
@SuppressLint("DefaultLocale")
@Composable
fun Gauge(
    modifier: Modifier = Modifier,
    inputValue: Float,
    minValue: Float = 0f,
    maxValue: Float = 100f,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColors: List<Color> = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),
    innerGradient: Color = MaterialTheme.colorScheme.primary,
    percentageColor: Color = MaterialTheme.colorScheme.primary,
    label: String = ""
) {

    val meterPercentage = getMeterPercentage(inputValue, minValue, maxValue)
    val formattedValue = String.format("%.0f", inputValue)

    Box(modifier = modifier.size(260.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepAngle = 240f
            val fillSwipeAngle = (meterPercentage / 100f) * sweepAngle
            val height = size.height
            val width = size.width
            val startAngle = 150f
            val arcHeight = height - 20.dp.toPx()

            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset((width - height + 60f) / 2f, (height - arcHeight) / 2f),
                size = Size(arcHeight, arcHeight),
                style = Stroke(width = 50f, cap = StrokeCap.Round)
            )

            drawArc(
                brush = Brush.horizontalGradient(progressColors),
                startAngle = startAngle,
                sweepAngle = fillSwipeAngle,
                useCenter = false,
                topLeft = Offset((width - height + 60f) / 2f, (height - arcHeight) / 2),
                size = Size(arcHeight, arcHeight),
                style = Stroke(width = 50f, cap = StrokeCap.Round)
            )
            val centerOffset = Offset(width / 2f, height / 2.09f)
            drawCircle(
                Brush.radialGradient(
                    listOf(
                        innerGradient.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ), width / 2f
            )
            drawCircle(Color.White, 24f, centerOffset)

            // Calculate needle angle based on inputValue
            val needleAngle = (meterPercentage / 100f) * sweepAngle + startAngle
            val needleLength = 160f // Adjust this value to control needle length
            val needleBaseWidth = 10f // Adjust this value to control the base width


            val needlePath = Path().apply {
                // Calculate the top point of the needle
                val topX = centerOffset.x + needleLength * cos(
                    Math.toRadians(needleAngle.toDouble()).toFloat()
                )
                val topY = centerOffset.y + needleLength * sin(
                    Math.toRadians(needleAngle.toDouble()).toFloat()
                )

                // Calculate the base points of the needle
                val baseLeftX = centerOffset.x + needleBaseWidth * cos(
                    Math.toRadians((needleAngle - 90).toDouble()).toFloat()
                )
                val baseLeftY = centerOffset.y + needleBaseWidth * sin(
                    Math.toRadians((needleAngle - 90).toDouble()).toFloat()
                )
                val baseRightX = centerOffset.x + needleBaseWidth * cos(
                    Math.toRadians((needleAngle + 90).toDouble()).toFloat()
                )
                val baseRightY = centerOffset.y + needleBaseWidth * sin(
                    Math.toRadians((needleAngle + 90).toDouble()).toFloat()
                )

                moveTo(topX, topY)
                lineTo(baseLeftX, baseLeftY)
                lineTo(baseRightX, baseRightY)
                close()
            }

            drawPath(
                color = Color.White,
                path = needlePath
            )
        }

        Column(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .align(Alignment.BottomCenter), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$formattedValue dB", fontSize = 20.sp, lineHeight = 28.sp, color = percentageColor)
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFFB0B4CD)
                )
            }
        }

    }
}

private fun getMeterPercentage(value: Float, min: Float, max: Float): Float {
    return if (value < 0f) {
        0f
    } else if (value > max) {
        100f
    } else {
        ((value - min)/max) * 100f
    }
}