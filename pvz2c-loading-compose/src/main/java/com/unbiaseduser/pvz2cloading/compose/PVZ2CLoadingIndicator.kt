package com.unbiaseduser.pvz2cloading.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PvZ2CLoadingOverlay(
    modifier: Modifier = Modifier,
    running: Boolean = true,
    text: String = "服务器通讯中...",
    orbitRadius: Dp = 4.dp,
    durationMillis: Int = 1000
) {
    val angle = remember { Animatable(0f) }

    LaunchedEffect(running) {
        if (!running) {
            return@LaunchedEffect
        }

        while (true) {
            val current = angle.value

            // Finish the current revolution at the same angular speed.
            val remainingFraction = (360f - current) / 360f
            val remainingDuration = (durationMillis * remainingFraction).roundToInt()

            angle.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = remainingDuration.coerceAtLeast(1),
                    easing = LinearEasing
                )
            )

            angle.snapTo(0f)
        }
    }

    val orbitPx = with(LocalDensity.current) {
        orbitRadius.toPx()
    }

    val radians = Math.toRadians(angle.value.toDouble())

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(color = Color(alpha = 64, red = 0, green = 0, blue = 0))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text, fontSize = 24.sp, color = Color.White)
            Box(modifier = Modifier.offset(y = (-4).dp)) {
                PvZ2CLoadingIndicator(
                    modifier = Modifier.graphicsLayer {
                        rotationZ = angle.value
                        translationX = orbitPx * cos(radians).toFloat()
                        translationY = orbitPx * sin(radians).toFloat()
                    }
                )
            }
        }
    }

}

@Composable
fun PvZ2CLoadingIndicator(
    modifier: Modifier = Modifier,
    strokeWidthPx: Float = 15f,
    arcSweepDeg: Float = 270f,
    tailFractionOfArc: Float = 0.15f,
    arcStartDeg: Float = 0f
) {
    Canvas(modifier = modifier.size(72.dp)
        .padding(4.dp)) {
        val radius = size.minDimension / 2f - strokeWidthPx * 1.5f
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2f, radius * 2f)

        val arcPortionOfFullCircle = arcSweepDeg / 360f
        val tailPortionOfFullCircle = arcPortionOfFullCircle * tailFractionOfArc

        val brush = Brush.sweepGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                tailPortionOfFullCircle * 0.45f to Color.White.copy(alpha = 0.30f),
                tailPortionOfFullCircle to Color.White,
                (arcPortionOfFullCircle - 0.001f) to Color.White,
                arcPortionOfFullCircle to Color.Transparent,
                1f to Color.Transparent
            ),
            center = center
        )

        // Rotate the drawing context so the brush's 0-degree direction
        // lines up with the arc start.
        rotate(degrees = arcStartDeg, pivot = center) {
            drawArc(
                brush = brush,
                startAngle = 0f,
                sweepAngle = arcSweepDeg,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Butt
                )
            )

            // Arrowhead at the arc end
            val endAngle = Math.toRadians(arcSweepDeg.toDouble())

            val radialX = cos(endAngle).toFloat()
            val radialY = sin(endAngle).toFloat()

            val tangentX = -radialY
            val tangentY = radialX

            val endPoint = Offset(
                x = center.x + radius * radialX,
                y = center.y + radius * radialY
            )

            val arrowLength = strokeWidthPx * 1.8f
            val arrowHalfWidth = strokeWidthPx * 1.6f
            val overlap = strokeWidthPx * 0.45f

            val baseCenter = Offset(
                x = endPoint.x - tangentX * overlap,
                y = endPoint.y - tangentY * overlap
            )

            val tip = Offset(
                x = baseCenter.x + tangentX * arrowLength,
                y = baseCenter.y + tangentY * arrowLength
            )

            val base1 = Offset(
                x = baseCenter.x + radialX * arrowHalfWidth,
                y = baseCenter.y + radialY * arrowHalfWidth
            )

            val base2 = Offset(
                x = baseCenter.x - radialX * arrowHalfWidth,
                y = baseCenter.y - radialY * arrowHalfWidth
            )

            val arrow = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(base1.x, base1.y)
                lineTo(base2.x, base2.y)
                close()
            }

            drawPath(arrow, Color.White)
        }
    }
}