package com.ambercatalbas.vaktinde.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambercatalbas.vaktinde.core.ui.R
import com.ambercatalbas.vaktinde.core.ui.theme.Dimens
import com.ambercatalbas.vaktinde.core.ui.theme.Gold

private val columnHeaders = listOf(
    "İmsak" to Icons.Default.DarkMode,
    "Güneş" to Icons.Default.WbSunny,
    "Öğle" to Icons.Default.LightMode,
    "İkindi" to Icons.Default.WbSunny,
    "Akşam" to Icons.Default.WbTwilight,
    "Yatsı" to Icons.Default.Nightlight,
)

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header row with title and city chip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.StandardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.calendar_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            // City chip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = state.cityName,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Month switcher
        MonthSwitcher(
            monthTitle = state.monthTitle,
            onPrevious = viewModel::goToPreviousMonth,
            onNext = viewModel::goToNextMonth,
            modifier = Modifier.padding(horizontal = Dimens.StandardPadding),
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Table
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = Dimens.StandardPadding)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
            ) {
                // Column headers
                TableHeader()
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                // Rows
                LazyColumn {
                    items(state.rows, key = { it.day }) { row ->
                        CalendarDayRow(row = row)
                        if (row != state.rows.last()) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun MonthSwitcher(
    monthTitle: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onPrevious,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Önceki ay",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = monthTitle,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.onSurface,
        )

        IconButton(
            onClick = onNext,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Sonraki ay",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(start = 52.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        columnHeaders.forEach { (label, icon) ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun CalendarDayRow(row: MonthRow) {
    val textColor = if (row.isToday) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val textWeight = if (row.isToday) FontWeight.SemiBold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .then(
                if (row.isToday) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Gold.copy(alpha = 0.18f),
                                Gold.copy(alpha = 0.08f),
                            )
                        )
                    )
                } else {
                    Modifier.background(MaterialTheme.colorScheme.surface)
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Today indicator bar
        if (row.isToday) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(46.dp)
                    .background(Gold),
            )
        }

        // Day column
        Column(
            modifier = Modifier
                .width(if (row.isToday) 49.dp else 52.dp)
                .padding(start = 14.dp),
        ) {
            Text(
                text = "${row.day}",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Serif,
            )
            Text(
                text = row.dayOfWeek,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontSize = 10.5.sp,
            )
        }

        // Prayer times
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        ) {
            row.times.forEach { time ->
                Text(
                    text = time,
                    color = textColor,
                    fontSize = 11.5.sp,
                    fontWeight = textWeight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
