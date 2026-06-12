package com.ambercatalbas.vaktinde.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import com.ambercatalbas.vaktinde.core.ui.R
import com.ambercatalbas.vaktinde.core.ui.theme.Dimens
import com.ambercatalbas.vaktinde.feature.home.components.HeroCountdownCard
import com.ambercatalbas.vaktinde.feature.home.components.HijriDateRow
import com.ambercatalbas.vaktinde.feature.home.components.PrayerRowState
import com.ambercatalbas.vaktinde.feature.home.components.PrayerTimeRow

@Composable
fun HomeScreen(
    onNavigateToCitySelection: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Precompute localized prayer names for share
    val prayerNames = mapOf(
        PrayerType.IMSAK to stringResource(R.string.prayer_imsak),
        PrayerType.GUNES to stringResource(R.string.prayer_gunes),
        PrayerType.OGLE to stringResource(R.string.prayer_ogle),
        PrayerType.IKINDI to stringResource(R.string.prayer_ikindi),
        PrayerType.AKSAM to stringResource(R.string.prayer_aksam),
        PrayerType.YATSI to stringResource(R.string.prayer_yatsi),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        TopBar(
            cityName = state.cityName,
            gregorianDate = state.gregorianDate,
            onCityClick = onNavigateToCitySelection,
            onNotificationsClick = onNavigateToNotifications,
            onShareClick = {
                val prayers = state.dailyPrayers?.prayers
                if (prayers != null) {
                    val shareText = buildString {
                        appendLine("${state.cityName} - ${state.gregorianDate}")
                        appendLine()
                        prayers.forEach { prayer ->
                            val name = prayerNames[prayer.type] ?: prayer.type.key
                            appendLine("$name: ${prayer.timeString}")
                        }
                        appendLine()
                        appendLine("Vaktinde")
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }
            },
        )

        // Hero countdown card
        if (!state.isLoading) {
            HeroCountdownCard(
                currentPrayerType = state.currentPrayerType,
                nextPrayer = state.nextPrayer,
                countdown = state.countdown,
                progress = state.progress,
                modifier = Modifier.padding(horizontal = Dimens.StandardPadding),
            )
        } else {
            // Loading placeholder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = Dimens.StandardPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hijri date row
        if (state.hijriDate.isNotEmpty()) {
            HijriDateRow(
                hijriDate = state.hijriDate,
                modifier = Modifier.padding(horizontal = Dimens.StandardPadding),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prayer list header
        Text(
            text = stringResource(R.string.home_todays_prayers),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = Dimens.StandardPadding + 6.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Prayer list card
        val prayers = state.dailyPrayers?.prayers
        if (prayers != null) {
            Column(
                modifier = Modifier
                    .padding(horizontal = Dimens.StandardPadding)
                    .clip(RoundedCornerShape(Dimens.CardRadius))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(Dimens.CardRadius),
                    )
                    .padding(8.dp),
            ) {
                prayers.forEachIndexed { index, prayer ->
                    val rowState = when {
                        index == state.nextPrayerIndex -> PrayerRowState.NEXT
                        state.nextPrayerIndex == -1 -> PrayerRowState.NORMAL
                        state.nextPrayerIndex == 0 -> {
                            // All passed or next is first (wrap around)
                            PrayerRowState.PAST
                        }
                        index < state.nextPrayerIndex -> PrayerRowState.PAST
                        else -> PrayerRowState.NORMAL
                    }
                    PrayerTimeRow(
                        prayer = prayer,
                        state = rowState,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun TopBar(
    cityName: String,
    gregorianDate: String,
    onCityClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.StandardPadding, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left: City and date
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable(onClick = onCityClick),
            ) {
                Text(
                    text = cityName,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.settings_city),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(20.dp),
                )
            }
            if (gregorianDate.isNotEmpty()) {
                Text(
                    text = gregorianDate,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                )
            }
        }

        // Right: Action buttons
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionButton(
                icon = Icons.Default.Share,
                onClick = onShareClick,
            )
            ActionButton(
                icon = Icons.Default.Notifications,
                onClick = onNotificationsClick,
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp),
        )
    }
}
