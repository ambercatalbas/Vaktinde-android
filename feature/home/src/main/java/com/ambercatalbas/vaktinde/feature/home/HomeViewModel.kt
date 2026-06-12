package com.ambercatalbas.vaktinde.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambercatalbas.vaktinde.core.domain.model.City
import com.ambercatalbas.vaktinde.core.domain.model.CountdownParts
import com.ambercatalbas.vaktinde.core.domain.model.DailyPrayers
import com.ambercatalbas.vaktinde.core.domain.model.Prayer
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import com.ambercatalbas.vaktinde.core.domain.repository.NotificationScheduler
import com.ambercatalbas.vaktinde.core.domain.repository.PrayerTimeRepository
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val dailyPrayers: DailyPrayers? = null,
    val currentPrayerType: PrayerType? = null,
    val nextPrayer: Prayer? = null,
    val nextPrayerIndex: Int = -1,
    val countdown: CountdownParts = CountdownParts("00", "00", "00"),
    val remainingSeconds: Long = 0,
    val progress: Double = 0.0,
    val hijriDate: String = "",
    val gregorianDate: String = "",
    val cityName: String = "İstanbul",
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationScheduler: NotificationScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val selectedCity = userPreferencesRepository.selectedCity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), City.default)

    init {
        observeCity()
        startCountdownTimer()
    }

    private fun observeCity() {
        viewModelScope.launch {
            selectedCity.collectLatest { city ->
                _uiState.update { it.copy(cityName = city.name, isLoading = true) }
                loadPrayers(city)
            }
        }
    }

    private suspend fun loadPrayers(city: City) {
        try {
            prayerTimeRepository.getDailyPrayers(city).collect { daily ->
                val hijri = formatHijriDate()
                val gregorian = LocalDate.now().format(
                    DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("tr"))
                )
                _uiState.update {
                    it.copy(
                        dailyPrayers = daily,
                        hijriDate = hijri,
                        gregorianDate = gregorian,
                        isLoading = false,
                        error = null,
                    )
                }
                updatePrayerState()
                notificationScheduler.scheduleToday(daily.prayers)
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                updatePrayerState()
            }
        }
    }

    private fun updatePrayerState() {
        val prayers = _uiState.value.dailyPrayers?.prayers ?: return
        val now = LocalTime.now()

        // Find next prayer
        var nextIndex = prayers.indexOfFirst { it.time.isAfter(now) }
        val currentIndex: Int
        val nextPrayer: Prayer

        if (nextIndex == -1) {
            // All prayers passed today - next is tomorrow's first prayer
            nextIndex = 0
            currentIndex = prayers.size - 1
            nextPrayer = prayers[0]
        } else {
            currentIndex = if (nextIndex > 0) nextIndex - 1 else prayers.size - 1
            nextPrayer = prayers[nextIndex]
        }

        // Calculate remaining seconds
        val nowSeconds = now.toSecondOfDay().toLong()
        val nextSeconds = nextPrayer.time.toSecondOfDay().toLong()
        val remaining = if (nextSeconds > nowSeconds) {
            nextSeconds - nowSeconds
        } else {
            // Next prayer is tomorrow
            (24 * 3600) - nowSeconds + nextSeconds
        }

        // Calculate progress between current and next prayer
        val currentPrayerTime = prayers[currentIndex].time
        val currentSeconds = currentPrayerTime.toSecondOfDay().toLong()
        val totalDuration = if (nextSeconds > currentSeconds) {
            nextSeconds - currentSeconds
        } else {
            (24 * 3600) - currentSeconds + nextSeconds
        }
        val elapsed = if (nowSeconds >= currentSeconds) {
            nowSeconds - currentSeconds
        } else {
            (24 * 3600) - currentSeconds + nowSeconds
        }
        val progress = if (totalDuration > 0) elapsed.toDouble() / totalDuration else 0.0

        val hours = remaining / 3600
        val minutes = (remaining % 3600) / 60
        val seconds = remaining % 60

        _uiState.update {
            it.copy(
                currentPrayerType = prayers[currentIndex].type,
                nextPrayer = nextPrayer,
                nextPrayerIndex = nextIndex,
                remainingSeconds = remaining,
                countdown = CountdownParts(
                    hours = "%02d".format(hours),
                    minutes = "%02d".format(minutes),
                    seconds = "%02d".format(seconds),
                ),
                progress = progress.coerceIn(0.0, 1.0),
            )
        }
    }

    private fun formatHijriDate(): String {
        return try {
            val hijri = HijrahDate.now()
            val day = hijri.get(ChronoField.DAY_OF_MONTH)
            val month = hijri.get(ChronoField.MONTH_OF_YEAR)
            val year = hijri.get(ChronoField.YEAR)
            val monthNames = listOf(
                "Muharrem", "Safer", "Rebiülevvel", "Rebiülahir",
                "Cemaziyelevvel", "Cemaziyelahir", "Recep", "Şaban",
                "Ramazan", "Şevval", "Zilkade", "Zilhicce"
            )
            val monthName = monthNames.getOrElse(month - 1) { "" }
            "$day $monthName $year"
        } catch (_: Exception) {
            ""
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                prayerTimeRepository.refreshPrayers(selectedCity.value)
                loadPrayers(selectedCity.value)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
