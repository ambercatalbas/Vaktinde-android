package com.ambercatalbas.vaktinde.feature.settings

import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambercatalbas.vaktinde.core.domain.model.CalcMethod
import com.ambercatalbas.vaktinde.core.ui.R
import com.ambercatalbas.vaktinde.core.ui.theme.Dimens
import com.ambercatalbas.vaktinde.core.ui.theme.Gold

@Composable
fun SettingsScreen(
    onNavigateToCitySelection: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.StandardPadding),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Brand card
        BrandCard()

        Spacer(modifier = Modifier.height(14.dp))

        // Preferences section
        SectionHeader(stringResource(R.string.settings_preferences))
        SettingsCard {
            SettingsRow(
                icon = Icons.Default.Language,
                title = stringResource(R.string.settings_language),
                detail = viewModel.languageDisplayName,
                onClick = { viewModel.showLanguageDialog(true) },
            )
            SettingsRow(
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.settings_theme),
                detail = viewModel.themeDisplayName,
                onClick = { viewModel.showThemeDialog(true) },
            )
            SettingsRow(
                icon = Icons.Default.Explore,
                title = stringResource(R.string.settings_calc_method),
                detail = viewModel.methodDisplayName,
                onClick = { viewModel.showMethodDialog(true) },
                showDivider = false,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Location section
        SectionHeader(stringResource(R.string.settings_location))
        SettingsCard {
            SettingsRow(
                icon = Icons.Default.LocationOn,
                title = stringResource(R.string.settings_city),
                detail = state.cityName,
                onClick = onNavigateToCitySelection,
                showDivider = false,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Notifications section
        SectionHeader(stringResource(R.string.settings_notifications))
        SettingsCard {
            SettingsRow(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.settings_notification_settings),
                detail = stringResource(R.string.settings_customize),
                onClick = onNavigateToNotifications,
                showDivider = false,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // About section
        SectionHeader(stringResource(R.string.settings_about))
        SettingsCard {
            SettingsRow(
                icon = Icons.Default.Info,
                title = stringResource(R.string.settings_about_app),
                onClick = { showAboutDialog = true },
            )
            SettingsRow(
                icon = Icons.Default.Share,
                title = stringResource(R.string.settings_share),
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Vaktinde")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Vaktinde - Namaz Vakitleri\nhttps://play.google.com/store/apps/details?id=com.ambercatalbas.vaktinde"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                },
                showDivider = false,
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // Theme dialog
    if (state.showThemeDialog) {
        SelectionDialog(
            title = stringResource(R.string.settings_theme),
            options = listOf(
                stringResource(R.string.theme_system) to "system",
                stringResource(R.string.theme_dark) to "dark",
                stringResource(R.string.theme_light) to "light",
            ),
            selectedValue = state.theme,
            onSelect = { viewModel.setTheme(it); viewModel.showThemeDialog(false) },
            onDismiss = { viewModel.showThemeDialog(false) },
        )
    }

    // Language dialog
    if (state.showLanguageDialog) {
        SelectionDialog(
            title = stringResource(R.string.settings_language),
            options = listOf("Türkçe" to "tr", "English" to "en", "العربية" to "ar"),
            selectedValue = state.language,
            onSelect = { viewModel.setLanguage(it); viewModel.showLanguageDialog(false) },
            onDismiss = { viewModel.showLanguageDialog(false) },
        )
    }

    // Calc method dialog
    if (state.showMethodDialog) {
        SelectionDialog(
            title = stringResource(R.string.settings_calc_method),
            options = CalcMethod.entries.map { method ->
                val name = when (method) {
                    CalcMethod.DIYANET -> stringResource(R.string.method_diyanet)
                    CalcMethod.MWL -> stringResource(R.string.method_mwl)
                    CalcMethod.ISNA -> stringResource(R.string.method_isna)
                    CalcMethod.EGYPT -> stringResource(R.string.method_egypt)
                    CalcMethod.UMM_AL_QURA -> stringResource(R.string.method_umm_al_qura)
                    CalcMethod.KARACHI -> stringResource(R.string.method_karachi)
                }
                name to method.key
            },
            selectedValue = state.calcMethod.key,
            onSelect = { key ->
                viewModel.setCalcMethod(CalcMethod.fromKey(key))
                viewModel.showMethodDialog(false)
            },
            onDismiss = { viewModel.showMethodDialog(false) },
        )
    }

    // About dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(R.string.settings_about_app)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Vaktinde v1.0.0",
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Namaz vakitleri, Kıble yönü ve Hicri takvim.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                    )
                    Text(
                        text = "© 2024 Amber Catalbas",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }
}

@Composable
private fun BrandCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.CardRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0D2138), Color(0xFF162840), Color(0xFF1A3050))
                )
            )
            .padding(22.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(52.dp),
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "Vaktinde",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "v1.0.0",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.5.sp,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
    ) {
        content()
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    detail: String? = null,
    onClick: () -> Unit,
    showDivider: Boolean = true,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )

            if (detail != null) {
                Text(
                    text = detail,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontSize = 14.5.sp,
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(18.dp),
            )
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 67.dp)
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outline),
            )
        }
    }
}

@Composable
private fun SelectionDialog(
    title: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEach { (label, value) ->
                    val isSelected = value == selectedValue
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isSelected) Modifier.background(Gold.copy(alpha = 0.12f))
                                else Modifier
                            )
                            .clickable { onSelect(value) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Gold else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f),
                        )
                        if (isSelected) {
                            Text(text = "✓", color = Gold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
    )
}
