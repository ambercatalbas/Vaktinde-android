package com.ambercatalbas.vaktinde.feature.qibla

import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambercatalbas.vaktinde.core.ui.R
import com.ambercatalbas.vaktinde.core.ui.theme.Dimens
import com.ambercatalbas.vaktinde.core.ui.theme.Gold
import com.ambercatalbas.vaktinde.core.ui.theme.GoldSoft
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun QiblaScreen(
    viewModel: QiblaViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        viewModel.startSensor(sensorManager)
        onDispose { viewModel.stopSensor() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimens.StandardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Text(
            text = stringResource(R.string.qibla_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Status pill
        StatusPill(isAligned = state.isAligned)

        Spacer(modifier = Modifier.height(20.dp))

        // Compass
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            CompassCanvas(
                heading = state.heading,
                qiblaBearing = state.qiblaBearing.toFloat(),
            )

            // Center hub with angle
            Box(
                modifier = Modifier
                    .size(94.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0C1626)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.bearingText,
                        color = Color.White,
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                    )
                    Text(
                        text = stringResource(R.string.qibla_angle),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hint text
        Text(
            text = if (state.isAligned) stringResource(R.string.qibla_aligned_hint) else stringResource(R.string.qibla_turn_hint),
            color = if (state.isAligned) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Info cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            InfoCard(
                icon = Icons.Default.LocationOn,
                value = state.cityName,
                label = stringResource(R.string.qibla_location),
                modifier = Modifier.weight(1f),
            )
            InfoCard(
                icon = Icons.Default.Navigation,
                value = state.bearingText,
                label = stringResource(R.string.qibla_direction),
                modifier = Modifier.weight(1f),
            )
            InfoCard(
                icon = Icons.Default.Straighten,
                value = state.distanceKm,
                label = stringResource(R.string.qibla_distance),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatusPill(isAligned: Boolean) {
    val bgColor = if (isAligned) Gold.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isAligned) Gold.copy(alpha = 0.38f) else MaterialTheme.colorScheme.outline
    val dotColor = if (isAligned) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val textColor = if (isAligned) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(color = dotColor)
        }
        Text(
            text = if (isAligned) stringResource(R.string.qibla_aligned) else stringResource(R.string.qibla_turn),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CompassCanvas(
    heading: Float,
    qiblaBearing: Float,
) {
    val outerRingDark = Color(0xFF162840)
    val innerCircleDark = Color(0xFF0C1626)
    val goldColor = Gold
    val goldDim = GoldSoft.copy(alpha = 0.6f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2
        val outerRadius = (size.minDimension / 2) - 20.dp.toPx()
        val ringWidth = 40.dp.toPx()
        val innerRadius = outerRadius - ringWidth

        // Rotate entire compass by negative heading
        rotate(-heading, pivot = Offset(cx, cy)) {
            // Outer ring
            drawCircle(outerRingDark, outerRadius, Offset(cx, cy))
            drawCircle(innerCircleDark, innerRadius, Offset(cx, cy))

            // Star field (18 stars within compass)
            val random = Random(77)
            repeat(18) {
                val angle = random.nextFloat() * 360f
                val dist = innerRadius * 0.15f + random.nextFloat() * innerRadius * 0.75f
                val rad = Math.toRadians(angle.toDouble()).toFloat()
                val sx = cx + dist * cos(rad)
                val sy = cy + dist * sin(rad)
                val opacity = 0.1f + random.nextFloat() * 0.25f
                val starRadius = 0.3f + random.nextFloat() * 0.7f
                drawCircle(
                    color = Color.White.copy(alpha = opacity),
                    radius = starRadius.dp.toPx(),
                    center = Offset(sx, sy),
                    style = Fill,
                )
            }

            // Tick marks - every 5 degrees (72 ticks), major every 45 degrees
            for (i in 0 until 72) {
                val deg = i * 5
                val rad = Math.toRadians(deg.toDouble() - 90).toFloat()
                val isMajor = deg % 45 == 0
                val is30 = deg % 30 == 0

                val (len, width, color) = when {
                    isMajor -> Triple(8.dp.toPx(), 2.5f.dp.toPx(), goldColor)
                    is30 -> Triple(13.dp.toPx(), 2f.dp.toPx(), goldDim)
                    deg % 10 == 0 -> Triple(9.dp.toPx(), 1.2f.dp.toPx(), Color.White.copy(alpha = 0.35f))
                    else -> Triple(5.dp.toPx(), 0.8f.dp.toPx(), Color.White.copy(alpha = 0.15f))
                }

                val startR = outerRadius - len
                val endR = outerRadius
                drawLine(
                    color = color,
                    start = Offset(cx + startR * cos(rad), cy + startR * sin(rad)),
                    end = Offset(cx + endR * cos(rad), cy + endR * sin(rad)),
                    strokeWidth = width,
                )
            }

            // Cardinal labels
            drawCardinalLabels(cx, cy, innerRadius, ringWidth, goldColor)

            // Qibla direction line
            val qiblaRad = Math.toRadians(qiblaBearing.toDouble() - 90).toFloat()
            drawLine(
                color = goldDim,
                start = Offset(
                    cx + innerRadius * 0.3f * cos(qiblaRad),
                    cy + innerRadius * 0.3f * sin(qiblaRad)
                ),
                end = Offset(
                    cx + (outerRadius - 4.dp.toPx()) * cos(qiblaRad),
                    cy + (outerRadius - 4.dp.toPx()) * sin(qiblaRad)
                ),
                strokeWidth = 1.5f.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(6.dp.toPx(), 5.dp.toPx())
                ),
            )

            // Kaaba marker on ring - rounded rect 38x38 with borderRadius 12
            val markerR = innerRadius + ringWidth / 2
            val markerX = cx + markerR * cos(qiblaRad)
            val markerY = cy + markerR * sin(qiblaRad)
            val markerSize = 38.dp.toPx()
            val markerCorner = 12.dp.toPx()

            // Draw rounded rect marker
            drawRoundRect(
                color = goldColor,
                topLeft = Offset(markerX - markerSize / 2, markerY - markerSize / 2),
                size = Size(markerSize, markerSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(markerCorner, markerCorner),
            )
            // Inner Kaaba icon (simplified box)
            val boxSize = 14.dp.toPx()
            drawRoundRect(
                color = innerCircleDark,
                topLeft = Offset(markerX - boxSize / 2, markerY - boxSize / 2),
                size = Size(boxSize, boxSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            )
        }

        // North indicator (fixed, doesn't rotate)
        val northTriangle = Path().apply {
            moveTo(cx, cy - outerRadius - 12.dp.toPx())
            lineTo(cx - 6.dp.toPx(), cy - outerRadius - 4.dp.toPx())
            lineTo(cx + 6.dp.toPx(), cy - outerRadius - 4.dp.toPx())
            close()
        }
        drawPath(northTriangle, goldColor, style = Fill)
    }
}

private fun DrawScope.drawCardinalLabels(
    cx: Float,
    cy: Float,
    innerRadius: Float,
    ringWidth: Float,
    goldColor: Color,
) {
    val labelR = innerRadius + ringWidth * 0.52f
    val cardinals = listOf(
        Triple(0f, "K", true),
        Triple(45f, "KD", false),
        Triple(90f, "D", false),
        Triple(135f, "GD", false),
        Triple(180f, "G", false),
        Triple(225f, "GB", false),
        Triple(270f, "B", false),
        Triple(315f, "KB", false),
    )
    val paint = android.graphics.Paint().apply {
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }

    cardinals.forEach { (deg, label, isNorth) ->
        val rad = Math.toRadians(deg.toDouble() - 90).toFloat()
        val x = cx + labelR * cos(rad)
        val y = cy + labelR * sin(rad)

        paint.apply {
            color = if (isNorth) {
                android.graphics.Color.rgb(224, 184, 106)
            } else {
                android.graphics.Color.argb(140, 255, 255, 255)
            }
            textSize = if (isNorth) 16.dp.toPx() else 13.dp.toPx()
            isFakeBoldText = isNorth
        }

        drawContext.canvas.nativeCanvas.drawText(
            label,
            x,
            y + paint.textSize / 3,
            paint,
        )
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(horizontal = 10.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(19.dp),
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontSize = 11.sp,
        )
    }
}
