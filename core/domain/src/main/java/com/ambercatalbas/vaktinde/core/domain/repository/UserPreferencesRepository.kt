package com.ambercatalbas.vaktinde.core.domain.repository

import com.ambercatalbas.vaktinde.core.domain.model.CalcMethod
import com.ambercatalbas.vaktinde.core.domain.model.City
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val selectedCity: Flow<City>
    val calcMethod: Flow<CalcMethod>
    val theme: Flow<String>
    val language: Flow<String>
    val hasCompletedOnboarding: Flow<Boolean>

    suspend fun setSelectedCity(city: City)
    suspend fun setCalcMethod(method: CalcMethod)
    suspend fun setTheme(theme: String)
    suspend fun setLanguage(language: String)
    suspend fun setOnboardingCompleted()
}
