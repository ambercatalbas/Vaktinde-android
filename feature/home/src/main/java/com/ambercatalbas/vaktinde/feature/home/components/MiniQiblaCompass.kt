package com.ambercatalbas.vaktinde.feature.home.components

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ambercatalbas.vaktinde.core.ui.theme.Gold
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val ALIGNMENT_THRESHOLD = 6.0

@Composable
fun MiniQiblaCompass(
    qiblaBearing: Double,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var heading by remember { mutableFloatStateOf(0f) }

    val rotationMatrix = remember { FloatArray(9) }
    val orientationAngles = remember { FloatArray(3) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    heading = (azimuth + 360) % 360
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val deviation = ((qiblaBearing - heading + 180) % 360 + 360) % 360 - 180
    val isAligned = abs(deviation) < ALIGNMENT_THRESHOLD

    val trackColor = Color.Gray.copy(alpha = 0.3f)
    val goldColor = Gold
    val arrowColor = if (isAligned) goldColor else Gold.copy(alpha = 0.7f)

    // Arc intensity based on proximity
    val arcAlpha = if (isAligned) 1f else (0.4f + 0.6f * (1f - (abs(deviation).toFloat() / 180f).coerceIn(0f, 1f)))

    Canvas(modifier = modifier.size(36.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val radius = (size.minDimension / 2) - 3.dp.toPx()
        val strokeWidth = 2.5f.dp.toPx()

        // Track circle (background ring)
        drawCircle(
            color = trackColor,
            radius = radius,
            center = Offset(cx, cy),
            style = Stroke(width = strokeWidth),
        )

        // Gold arc - shows how close to Qibla direction
        if (!isAligned) {
            val sweepAngle = (360f * (1f - abs(deviation).toFloat() / 180f)).coerceIn(20f, 340f)
            val startAngle = -90f - sweepAngle / 2f

            drawArc(
                color = goldColor.copy(alpha = arcAlpha),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        } else {
            // Full gold circle when aligned
            drawCircle(
                color = goldColor,
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = strokeWidth),
            )
        }

        // Navigation arrow pointing toward Qibla
        val angle = Math.toRadians(deviation).toFloat()
        val arrowLen = radius * 0.6f
        val arrowHalfWidth = 4.dp.toPx()
        val tailLen = radius * 0.25f

        // Tip of the arrow
        val tipX = cx + arrowLen * sin(angle)
        val tipY = cy - arrowLen * cos(angle)

        // Left and right base points of the arrow head
        val baseLeftX = cx + tailLen * sin(angle) - arrowHalfWidth * cos(angle)
        val baseLeftY = cy - tailLen * cos(angle) - arrowHalfWidth * sin(angle)
        val baseRightX = cx + tailLen * sin(angle) + arrowHalfWidth * cos(angle)
        val baseRightY = cy - tailLen * cos(angle) + arrowHalfWidth * sin(angle)

        // Center notch (makes a chevron shape)
        val notchX = cx + (tailLen + arrowLen) / 2.6f * sin(angle)
        val notchY = cy - (tailLen + arrowLen) / 2.6f * cos(angle)

        val arrowPath = Path().apply {
            moveTo(tipX, tipY)
            lineTo(baseLeftX, baseLeftY)
            lineTo(notchX, notchY)
            lineTo(baseRightX, baseRightY)
            close()
        }
        drawPath(arrowPath, color = arrowColor)

        // Small center dot
        drawCircle(
            color = arrowColor.copy(alpha = 0.5f),
            radius = 2.dp.toPx(),
            center = Offset(cx, cy),
        )
    }
}
