package com.ambercatalbas.vaktinde.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambercatalbas.vaktinde.core.domain.model.CalcMethod
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val theme: String = "system",
    val language: String = "tr",
    val calcMethod: CalcMethod = CalcMethod.DIYANET,
    val cityName: String = "İstanbul",
    val showThemeDialog: Boolean = false,
    val showLanguageDialog: Boolean = false,
    val showMethodDialog: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.theme,
                userPreferencesRepository.language,
                userPreferencesRepository.calcMethod,
                userPreferencesRepository.selectedCity,
            ) { theme, language, method, city ->
                SettingsUiState(
                    theme = theme,
                    language = language,
                    calcMethod = method,
                    cityName = city.name,
                )
            }.collect { state ->
                _uiState.update {
                    state.copy(
                        showThemeDialog = it.showThemeDialog,
                        showLanguageDialog = it.showLanguageDialog,
                        showMethodDialog = it.showMethodDialog,
                    )
                }
            }
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { userPreferencesRepository.setTheme(theme) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { userPreferencesRepository.setLanguage(language) }
    }

    fun setCalcMethod(method: CalcMethod) {
        viewModelScope.launch { userPreferencesRepository.setCalcMethod(method) }
    }

    fun showThemeDialog(show: Boolean) {
        _uiState.update { it.copy(showThemeDialog = show) }
    }

    fun showLanguageDialog(show: Boolean) {
        _uiState.update { it.copy(showLanguageDialog = show) }
    }

    fun showMethodDialog(show: Boolean) {
        _uiState.update { it.copy(showMethodDialog = show) }
    }

    val themeDisplayName: String
        get() = when (_uiState.value.theme) {
            "dark" -> "Koyu"
            "light" -> "Açık"
            else -> "Sistem"
        }

    val languageDisplayName: String
        get() = when (_uiState.value.language) {
            "en" -> "English"
            "ar" -> "العربية"
            else -> "Türkçe"
        }

    val methodDisplayName: String
        get() = when (_uiState.value.calcMethod) {
            CalcMethod.DIYANET -> "Diyanet"
            CalcMethod.MWL -> "MWL"
            CalcMethod.ISNA -> "ISNA"
            CalcMethod.EGYPT -> "Mısır"
            CalcMethod.UMM_AL_QURA -> "Umm Al-Qura"
            CalcMethod.KARACHI -> "Karachi"
        }
}
