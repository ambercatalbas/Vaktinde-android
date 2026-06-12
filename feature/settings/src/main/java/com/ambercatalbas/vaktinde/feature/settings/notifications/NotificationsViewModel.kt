package com.ambercatalbas.vaktinde.feature.settings.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambercatalbas.vaktinde.core.domain.model.AdhanSound
import com.ambercatalbas.vaktinde.core.domain.model.NotificationMode
import com.ambercatalbas.vaktinde.core.domain.model.NotificationPreferences
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import com.ambercatalbas.vaktinde.core.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val preferences: NotificationPreferences = NotificationPreferences(),
    val expandedPrayer: PrayerType? = null,
    val showPreReminderTimePicker: Boolean = false,
    val showSoundPickerForPrayer: PrayerType? = null,
    val showPreReminderSoundPicker: Boolean = false,
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            notificationRepository.preferences.collect { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
    }

    fun setMasterEnabled(enabled: Boolean) {
        viewModelScope.launch { notificationRepository.setMasterEnabled(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { notificationRepository.setVibrationEnabled(enabled) }
    }

    fun setFullAdhanEnabled(enabled: Boolean) {
        viewModelScope.launch { notificationRepository.setFullAdhanEnabled(enabled) }
    }

    fun setPrayerMode(prayerType: PrayerType, mode: NotificationMode) {
        viewModelScope.launch { notificationRepository.setPrayerMode(prayerType, mode) }
    }

    fun setPrayerSound(prayerType: PrayerType, soundId: String) {
        viewModelScope.launch { notificationRepository.setPrayerSound(prayerType, soundId) }
    }

    fun setPreReminderMinutes(minutes: Int) {
        viewModelScope.launch { notificationRepository.setPreReminderMinutes(minutes) }
    }

    fun setPreReminderSoundId(soundId: String) {
        viewModelScope.launch { notificationRepository.setPreReminderSoundId(soundId) }
    }

    fun toggleExpandedPrayer(prayerType: PrayerType) {
        _uiState.update {
            it.copy(expandedPrayer = if (it.expandedPrayer == prayerType) null else prayerType)
        }
    }

    fun showPreReminderTimePicker(show: Boolean) {
        _uiState.update { it.copy(showPreReminderTimePicker = show) }
    }

    fun showSoundPickerForPrayer(prayerType: PrayerType?) {
        _uiState.update { it.copy(showSoundPickerForPrayer = prayerType) }
    }

    fun showPreReminderSoundPicker(show: Boolean) {
        _uiState.update { it.copy(showPreReminderSoundPicker = show) }
    }
}
