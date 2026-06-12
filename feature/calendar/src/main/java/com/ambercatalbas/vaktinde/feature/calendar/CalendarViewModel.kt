package com.ambercatalbas.vaktinde.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambercatalbas.vaktinde.core.domain.model.City
import com.ambercatalbas.vaktinde.core.domain.model.PrayerDayTimes
import com.ambercatalbas.vaktinde.core.domain.model.PrayerType
import com.ambercatalbas.vaktinde.core.domain.repository.PrayerTimeRepository
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class MonthRow(
    val day: Int,
    val dayOfWeek: String,
    val times: List<String>,
    val isToday: Boolean,
)

data class CalendarUiState(
    val monthTitle: String = "",
    val rows: List<MonthRow> = emptyList(),
    val cityName: String = "İstanbul",
    val isLoading: Boolean = true,
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val prayerTimeRepository: PrayerTimeRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val selectedCity = userPreferencesRepository.selectedCity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), City.default)

    private var currentYearMonth = YearMonth.now()

    init {
        viewModelScope.launch {
            selectedCity.collect { city ->
                _uiState.update { it.copy(cityName = city.name) }
                loadMonth(city)
            }
        }
    }

    fun goToPreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1)
        viewModelScope.launch { loadMonth(selectedCity.value) }
    }

    fun goToNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1)
        viewModelScope.launch { loadMonth(selectedCity.value) }
    }

    private suspend fun loadMonth(city: City) {
        _uiState.update { it.copy(isLoading = true) }

        val title = currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale("tr"))
            .replaceFirstChar { it.uppercase() } + " ${currentYearMonth.year}"

        try {
            val monthlyData = prayerTimeRepository.getMonthlyPrayers(
                city = city,
                month = currentYearMonth.monthValue,
                year = currentYearMonth.year,
            )

            val today = LocalDate.now()
            val rows = buildRows(monthlyData, today)

            _uiState.update {
                it.copy(
                    monthTitle = title,
                    rows = rows,
                    isLoading = false,
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(monthTitle = title, rows = emptyList(), isLoading = false) }
        }
    }

    private fun buildRows(data: List<PrayerDayTimes>, today: LocalDate): List<MonthRow> {
        return data.map { day ->
            val date = try {
                LocalDate.parse(day.date)
            } catch (_: Exception) {
                null
            }
            val dayOfWeek = date?.dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale("tr")) ?: ""

            MonthRow(
                day = date?.dayOfMonth ?: 0,
                dayOfWeek = dayOfWeek,
                times = PrayerType.entries.map { day.time(it) },
                isToday = date == today,
            )
        }
    }
}
