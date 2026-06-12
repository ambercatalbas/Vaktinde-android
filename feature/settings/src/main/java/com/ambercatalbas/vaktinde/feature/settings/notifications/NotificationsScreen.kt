package com.ambercatalbas.vaktinde.feature.settings.notifications

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ambercatalbas.vaktinde.core.domain.model.AdhanSound
import com.ambercatalbas.vaktinde.core.domain.model.NotificationMode
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import com.ambercatalbas.vaktinde.core.ui.R
import com.ambercatalbas.vaktinde.core.ui.theme.Gold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val prefs = state.preferences
    val context = LocalContext.current

    var notifPermissionDenied by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.setMasterEnabled(true)
        } else {
            notifPermissionDenied = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.notif_title),
                    fontWeight = FontWeight.SemiBold,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            // General section
            SectionLabel(stringResource(R.string.notif_general))
            SettingsCard {
                SwitchRow(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.notif_master),
                    subtitle = stringResource(R.string.notif_master_desc),
                    checked = prefs.masterEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.setMasterEnabled(enabled)
                        }
                    },
                )
                Divider()
                SwitchRow(
                    icon = Icons.Default.Vibration,
                    title = stringResource(R.string.notif_vibration),
                    checked = prefs.vibrationEnabled,
                    onCheckedChange = viewModel::setVibrationEnabled,
                    enabled = prefs.masterEnabled,
                )
                Divider()
                SwitchRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    title = stringResource(R.string.notif_full_adhan),
                    subtitle = stringResource(R.string.notif_full_adhan_desc),
                    checked = prefs.fullAdhanEnabled,
                    onCheckedChange = viewModel::setFullAdhanEnabled,
                    enabled = prefs.masterEnabled,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prayer notifications section
            SectionLabel(stringResource(R.string.notif_prayer_settings))
            SettingsCard {
                val prayerTypes = PrayerType.entries
                prayerTypes.forEachIndexed { index, prayerType ->
                    val mode = prefs.modeFor(prayerType)
                    val sound = prefs.soundFor(prayerType)
                    val isExpanded = state.expandedPrayer == prayerType

                    PrayerNotificationRow(
                        prayerType = prayerType,
                        mode = mode,
                        soundName = adhanSoundDisplayName(sound.id),
                        isExpanded = isExpanded,
                        enabled = prefs.masterEnabled,
                        onToggleExpand = { viewModel.toggleExpandedPrayer(prayerType) },
                        onModeSelected = { viewModel.setPrayerMode(prayerType, it) },
                        onSoundClick = { viewModel.showSoundPickerForPrayer(prayerType) },
                    )

                    if (index < prayerTypes.size - 1) {
                        Divider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pre-reminder section
            SectionLabel(stringResource(R.string.notif_pre_reminder))
            SettingsCard {
                PreReminderRow(
                    title = stringResource(R.string.notif_pre_reminder_time),
                    value = if (prefs.preReminderMinutes > 0) {
                        stringResource(R.string.notif_pre_reminder_min, prefs.preReminderMinutes)
                    } else {
                        stringResource(R.string.notif_pre_reminder_off)
                    },
                    enabled = prefs.masterEnabled,
                    onClick = { viewModel.showPreReminderTimePicker(true) },
                )
                Divider()
                PreReminderRow(
                    title = stringResource(R.string.notif_pre_reminder_sound),
                    value = adhanSoundDisplayName(prefs.preReminderSoundId),
                    enabled = prefs.masterEnabled && prefs.preReminderMinutes > 0,
                    onClick = { viewModel.showPreReminderSoundPicker(true) },
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Pre-reminder time picker dialog
    if (state.showPreReminderTimePicker) {
        val options = listOf(0, 5, 10, 15, 20, 30)
        AlertDialog(
            onDismissRequest = { viewModel.showPreReminderTimePicker(false) },
            title = { Text(stringResource(R.string.notif_pre_reminder_time)) },
            text = {
                Column {
                    options.forEach { minutes ->
                        val label = if (minutes == 0) {
                            stringResource(R.string.notif_pre_reminder_off)
                        } else {
                            stringResource(R.string.notif_pre_reminder_min, minutes)
                        }
                        val isSelected = prefs.preReminderMinutes == minutes
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (isSelected) Modifier.background(Gold.copy(alpha = 0.12f))
                                    else Modifier
                                )
                                .clickable {
                                    viewModel.setPreReminderMinutes(minutes)
                                    viewModel.showPreReminderTimePicker(false)
                                }
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
                TextButton(onClick = { viewModel.showPreReminderTimePicker(false) }) {
                    Text(stringResource(R.string.close))
                }
            },
        )
    }

    // Sound selection dialog for a prayer type
    state.showSoundPickerForPrayer?.let { prayerType ->
        SoundSelectionDialog(
            currentSoundId = prefs.prayerSounds[prayerType] ?: AdhanSound.DEFAULT.id,
            onSelect = { soundId ->
                viewModel.setPrayerSound(prayerType, soundId)
                viewModel.showSoundPickerForPrayer(null)
            },
            onDismiss = { viewModel.showSoundPickerForPrayer(null) },
        )
    }

    // Sound selection dialog for pre-reminder
    if (state.showPreReminderSoundPicker) {
        SoundSelectionDialog(
            currentSoundId = prefs.preReminderSoundId,
            onSelect = { soundId ->
                viewModel.setPreReminderSoundId(soundId)
                viewModel.showPreReminderSoundPicker(false)
            },
            onDismiss = { viewModel.showPreReminderSoundPicker(false) },
        )
    }

    // Permission denied dialog
    if (notifPermissionDenied) {
        AlertDialog(
            onDismissRequest = { notifPermissionDenied = false },
            title = { Text(stringResource(R.string.notif_permission_denied)) },
            confirmButton = {
                TextButton(onClick = {
                    notifPermissionDenied = false
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }) {
                    Text(stringResource(R.string.notif_permission_open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { notifPermissionDenied = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
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
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 56.dp)
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.outline),
    )
}

@Composable
private fun SwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
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
        Spacer(modifier = Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 0.5f else 0.3f),
                    fontSize = 13.sp,
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Gold,
                checkedThumbColor = MaterialTheme.colorScheme.surface,
            ),
        )
    }
}

@Composable
private fun PrayerNotificationRow(
    prayerType: PrayerType,
    mode: NotificationMode,
    soundName: String,
    isExpanded: Boolean,
    enabled: Boolean,
    onToggleExpand: () -> Unit,
    onModeSelected: (NotificationMode) -> Unit,
    onSoundClick: () -> Unit,
) {
    val prayerName = prayerDisplayName(prayerType)
    val modeLabel = modeDisplayName(mode)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled, onClick = onToggleExpand)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = when (mode) {
                    NotificationMode.ADHAN -> Icons.Default.Notifications
                    NotificationMode.SILENT -> Icons.Default.Notifications
                    NotificationMode.OFF -> Icons.Default.NotificationsOff
                },
                contentDescription = null,
                tint = when (mode) {
                    NotificationMode.ADHAN -> Gold
                    NotificationMode.SILENT -> MaterialTheme.colorScheme.primary
                    NotificationMode.OFF -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prayerName,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = modeLabel,
                    color = when (mode) {
                        NotificationMode.ADHAN -> Gold.copy(alpha = 0.8f)
                        NotificationMode.SILENT -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        NotificationMode.OFF -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    },
                    fontSize = 13.sp,
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown
                else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp),
            )
        }

        AnimatedVisibility(
            visible = isExpanded && enabled,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Mode selector chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    NotificationMode.entries.forEach { m ->
                        ModeChip(
                            label = modeDisplayName(m),
                            isSelected = mode == m,
                            onClick = { onModeSelected(m) },
                        )
                    }
                }

                // Sound selector (only if mode is ADHAN)
                if (mode == NotificationMode.ADHAN) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(onClick = onSoundClick)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = soundName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) Gold else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface

    Text(
        text = label,
        color = textColor,
        fontSize = 13.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun PreReminderRow(
    title: String,
    value: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 1f else 0.4f),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 0.5f else 0.3f),
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun SoundSelectionDialog(
    currentSoundId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.notif_sound_selection)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.notif_sound_builtin),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                AdhanSound.DEFAULT_SOUNDS.forEach { sound ->
                    val isSelected = sound.id == currentSoundId
                    val displayName = adhanSoundDisplayName(sound.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isSelected) Modifier.background(Gold.copy(alpha = 0.12f))
                                else Modifier
                            )
                            .clickable { onSelect(sound.id) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (isSelected) Gold else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = displayName,
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

@Composable
private fun prayerDisplayName(type: PrayerType): String = when (type) {
    PrayerType.IMSAK -> stringResource(R.string.prayer_imsak)
    PrayerType.GUNES -> stringResource(R.string.prayer_gunes)
    PrayerType.OGLE -> stringResource(R.string.prayer_ogle)
    PrayerType.IKINDI -> stringResource(R.string.prayer_ikindi)
    PrayerType.AKSAM -> stringResource(R.string.prayer_aksam)
    PrayerType.YATSI -> stringResource(R.string.prayer_yatsi)
}

@Composable
private fun modeDisplayName(mode: NotificationMode): String = when (mode) {
    NotificationMode.ADHAN -> stringResource(R.string.notif_mode_adhan)
    NotificationMode.SILENT -> stringResource(R.string.notif_mode_silent)
    NotificationMode.OFF -> stringResource(R.string.notif_mode_off)
}

@Composable
private fun adhanSoundDisplayName(soundId: String): String = when (soundId) {
    "sabah_ezan" -> stringResource(R.string.notif_sound_sabah)
    "ogle_ezan" -> stringResource(R.string.notif_sound_ogle)
    "ikindi_ezan" -> stringResource(R.string.notif_sound_ikindi)
    "aksam_ezan" -> stringResource(R.string.notif_sound_aksam)
    "yatsi_ezan" -> stringResource(R.string.notif_sound_yatsi)
    "sela" -> stringResource(R.string.notif_sound_sela)
    else -> soundId
}
