package com.ambercatalbas.vaktinde.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ambercatalbas.vaktinde.core.domain.model.Prayer
import com.ambercatalbas.vaktinde.core.ui.theme.Gold
import com.ambercatalbas.vaktinde.core.ui.theme.GoldSoft

enum class PrayerRowState { PAST, NORMAL, NEXT }

@Composable
fun PrayerTimeRow(
    prayer: Prayer,
    state: PrayerRowState,
    modifier: Modifier = Modifier,
) {
    val alpha = if (state == PrayerRowState.PAST) 0.5f else 1f
    val backgroundColor = when (state) {
        PrayerRowState.NEXT -> Gold.copy(alpha = 0.16f)
        else -> Color.Transparent
    }
    val borderColor = when (state) {
        PrayerRowState.NEXT -> Gold.copy(alpha = 0.38f)
        else -> Color.Transparent
    }
    val iconContainerColor = when (state) {
        PrayerRowState.NEXT -> Gold.copy(alpha = 0.16f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val iconTint = when (state) {
        PrayerRowState.NEXT -> GoldSoft
        else -> MaterialTheme.colorScheme.primary
    }
    val nameColor = when (state) {
        PrayerRowState.NEXT -> GoldSoft
        else -> MaterialTheme.colorScheme.onSurface
    }
    val timeColor = when (state) {
        PrayerRowState.NEXT -> GoldSoft
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .then(
                if (state == PrayerRowState.NEXT) {
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(18.dp))
                } else Modifier
            )
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(iconContainerColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = prayerIcon(prayer.type),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
        }

        // Name and subtitle
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = prayerDisplayName(prayer.type),
                color = nameColor,
                fontSize = 16.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = prayerSubtitle(prayer.type),
                color = nameColor.copy(alpha = 0.6f),
                fontSize = 12.5.sp,
            )
        }

        // Badge (only for NEXT)
        if (state == PrayerRowState.NEXT) {
            Text(
                text = "SONRAKİ",
                color = GoldSoft,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Gold.copy(alpha = 0.14f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }

        // Time
        Text(
            text = prayer.timeString,
            color = timeColor,
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun prayerSubtitle(type: com.ambercatalbas.vaktinde.core.domain.model.PrayerType): String = when (type) {
    com.ambercatalbas.vaktinde.core.domain.model.PrayerType.IMSAK -> "Fajr"
    com.ambercatalbas.vaktinde.core.domain.model.PrayerType.GUNES -> "Sunrise"
    com.ambercatalbas.vaktinde.core.domain.model.PrayerType.OGLE -> "Dhuhr"
    com.ambercatalbas.vaktinde.core.domain.model.PrayerType.IKINDI -> "Asr"
    com.ambercatalbas.vaktinde.core.domain.model.PrayerType.AKSAM -> "Maghrib"
    com.ambercatalbas.vaktinde.core.domain.model.PrayerType.YATSI -> "Isha"
}
