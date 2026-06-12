package com.ambercatalbas.vaktinde.feature.settings.city

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambercatalbas.vaktinde.core.domain.model.City
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CitySelectionUiState(
    val searchQuery: String = "",
    val popularCities: List<City> = City.popular,
    val filteredCities: List<City> = emptyList(),
    val selectedCityId: String = City.default.id,
)

@HiltViewModel
class CitySelectionViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitySelectionUiState())
    val uiState: StateFlow<CitySelectionUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            userPreferencesRepository.selectedCity.collect { city ->
                _uiState.update { it.copy(selectedCityId = city.id) }
            }
        }

        @OptIn(FlowPreview::class)
        searchQueryFlow
            .debounce(300)
            .distinctUntilChanged()
            .map { query -> filterCities(query) }
            .onEach { filtered ->
                _uiState.update { it.copy(filteredCities = filtered) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryFlow.value = query
    }

    fun selectCity(city: City) {
        viewModelScope.launch {
            userPreferencesRepository.setSelectedCity(city)
        }
    }

    private fun filterCities(query: String): List<City> {
        if (query.isBlank()) return emptyList()
        val lower = query.lowercase()
        return City.popular.filter {
            it.name.lowercase().contains(lower) || it.region.lowercase().contains(lower)
        }
    }
}
