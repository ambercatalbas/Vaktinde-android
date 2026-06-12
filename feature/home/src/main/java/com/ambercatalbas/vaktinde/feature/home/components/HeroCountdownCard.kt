package com.ambercatalbas.vaktinde.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ambercatalbas.vaktinde.core.domain.model.CountdownParts
import com.ambercatalbas.vaktinde.core.domain.model.Prayer
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import kotlin.random.Random

private val TwilightBlue1 = Color(0xFF234468)
private val TwilightBlue2 = Color(0xFF16304E)
private val TwilightBlue3 = Color(0xFF0D2138)
private val TwilightBlue4 = Color(0xFF0A1A2E)
private val GoldAccent = Color(0xFFF0D4A0)
private val TextLight = Color(0xFFEEF4FB)
private val TextDim = Color(0xFFDCE8F6)
private val GoldGlow = Color(0xFFE0B86A)

@Composable
fun HeroCountdownCard(
    currentPrayerType: PrayerType?,
    nextPrayer: Prayer?,
    countdown: CountdownParts,
    progress: Double,
    modifier: Modifier = Modifier,
) {
    if (currentPrayerType == null || nextPrayer == null) return

    val hasHours = countdown.hours != "00"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(34.dp))
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to TwilightBlue1,
                            0.38f to TwilightBlue2,
                            0.70f to TwilightBlue3,
                            1f to TwilightBlue4,
                        ),
                        center = Offset(Float.MAX_VALUE * 0.78f, 0f),
                        radius = 1200f,
                    )
                )
        )

        // Star field
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = Random(42)
            repeat(16) {
                val x = random.nextFloat() * size.width
                val y = random.nextFloat() * size.height
                val opacity = 0.2f + random.nextFloat() * 0.5f
                val radius = 0.4f + random.nextFloat() * 1f
                drawCircle(
                    color = Color.White.copy(alpha = opacity),
                    radius = radius.dp.toPx(),
                    center = Offset(x, y),
                    style = Fill,
                )
            }
        }

        // Crescent moon
        Icon(
            imageVector = Icons.Default.Nightlight,
            contentDescription = null,
            tint = GoldAccent.copy(alpha = 0.3f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 14.dp)
                .size(90.dp)
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp, end = 26.dp, top = 26.dp, bottom = 22.dp)
        ) {
            // Current prayer indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                )
                Text(
                    text = "ŞU AN · ${prayerDisplayName(currentPrayerType).uppercase()}",
                    color = TextDim.copy(alpha = 0.7f),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp,
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Next prayer info
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = prayerIcon(nextPrayer.type),
                    contentDescription = null,
                    tint = GoldAccent.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(28.dp)
                        .padding(top = 4.dp),
                )
                Column {
                    Text(
                        text = "SONRAKİ NAMAZ",
                        color = TextDim.copy(alpha = 0.65f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    )
                    Text(
                        text = prayerDisplayName(nextPrayer.type),
                        color = TextLight,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                    )
                    Text(
                        text = nextPrayer.timeString,
                        color = GoldAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Big countdown
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (hasHours) {
                    Text(
                        text = "${countdown.hours}:${countdown.minutes}",
                        color = TextLight,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 60.sp,
                    )
                    Text(
                        text = ":${countdown.seconds}",
                        color = TextDim.copy(alpha = 0.6f),
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 30.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                } else {
                    Text(
                        text = countdown.minutes,
                        color = TextLight,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 60.sp,
                    )
                    Text(
                        text = ":${countdown.seconds}",
                        color = TextDim.copy(alpha = 0.6f),
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 30.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                Text(
                    text = if (hasHours) "saat" else "dakika",
                    color = TextDim.copy(alpha = 0.6f),
                    fontSize = 13.5.sp,
                    modifier = Modifier.padding(start = 10.dp, bottom = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.toFloat())
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFC89A4D), GoldAccent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val currentPrayer = prayerDisplayName(currentPrayerType)
                Text(
                    text = currentPrayer,
                    color = TextDim.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                )
                Text(
                    text = "${prayerDisplayName(nextPrayer.type)} ${nextPrayer.timeString}",
                    color = TextDim.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}

fun prayerDisplayName(type: PrayerType): String = when (type) {
    PrayerType.IMSAK -> "İmsak"
    PrayerType.GUNES -> "Güneş"
    PrayerType.OGLE -> "Öğle"
    PrayerType.IKINDI -> "İkindi"
    PrayerType.AKSAM -> "Akşam"
    PrayerType.YATSI -> "Yatsı"
}

fun prayerIcon(type: PrayerType): ImageVector = when (type) {
    PrayerType.IMSAK -> Icons.Default.DarkMode
    PrayerType.GUNES -> Icons.Default.WbSunny
    PrayerType.OGLE -> Icons.Default.LightMode
    PrayerType.IKINDI -> Icons.Default.WbSunny
    PrayerType.AKSAM -> Icons.Default.WbTwilight
    PrayerType.YATSI -> Icons.Default.Nightlight
}
