package com.ambercatalbas.vaktinde.core.data.repository

import com.ambercatalbas.vaktinde.core.data.datastore.UserPreferencesDataStore
import com.ambercatalbas.vaktinde.core.domain.model.CalcMethod
import com.ambercatalbas.vaktinde.core.domain.model.City
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore,
) : UserPreferencesRepository {

    override val selectedCity: Flow<City> = dataStore.selectedCity
    override val calcMethod: Flow<CalcMethod> = dataStore.calcMethod
    override val theme: Flow<String> = dataStore.theme
    override val language: Flow<String> = dataStore.language
    override val hasCompletedOnboarding: Flow<Boolean> = dataStore.hasCompletedOnboarding

    override suspend fun setSelectedCity(city: City) = dataStore.setSelectedCity(city)
    override suspend fun setCalcMethod(method: CalcMethod) = dataStore.setCalcMethod(method)
    override suspend fun setTheme(theme: String) = dataStore.setTheme(theme)
    override suspend fun setLanguage(language: String) = dataStore.setLanguage(language)
    override suspend fun setOnboardingCompleted() = dataStore.setOnboardingCompleted()
}
