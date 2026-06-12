package com.ambercatalbas.vaktinde.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ambercatalbas.vaktinde.core.domain.model.CalcMethod
import com.ambercatalbas.vaktinde.core.domain.model.City
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson,
) {
    private object Keys {
        val SELECTED_CITY = stringPreferencesKey("selected_city")
        val CALC_METHOD = stringPreferencesKey("calc_method")
        val THEME = stringPreferencesKey("theme")
        val LANGUAGE = stringPreferencesKey("language")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    val selectedCity: Flow<City> = dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_CITY]?.let { json ->
            try {
                gson.fromJson(json, City::class.java)
            } catch (_: Exception) {
                City.default
            }
        } ?: City.default
    }

    val calcMethod: Flow<CalcMethod> = dataStore.data.map { prefs ->
        CalcMethod.fromKey(prefs[Keys.CALC_METHOD] ?: CalcMethod.DIYANET.key)
    }

    val theme: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.THEME] ?: "system"
    }

    val language: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE] ?: "tr"
    }

    val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.HAS_COMPLETED_ONBOARDING] ?: false
    }

    suspend fun setSelectedCity(city: City) {
        dataStore.edit { prefs ->
            prefs[Keys.SELECTED_CITY] = gson.toJson(city)
        }
    }

    suspend fun setCalcMethod(method: CalcMethod) {
        dataStore.edit { prefs ->
            prefs[Keys.CALC_METHOD] = method.key
        }
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME] = theme
        }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language
        }
    }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[Keys.HAS_COMPLETED_ONBOARDING] = true
        }
    }
}
