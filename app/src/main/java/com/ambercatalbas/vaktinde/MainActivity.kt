package com.ambercatalbas.vaktinde

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import com.ambercatalbas.vaktinde.core.ui.theme.VaktindeTheme
import com.ambercatalbas.vaktinde.navigation.VaktindeApp
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by userPreferencesRepository.theme.collectAsStateWithLifecycle(initialValue = "system")
            val language by userPreferencesRepository.language.collectAsStateWithLifecycle(initialValue = "tr")

            applyLocale(language)

            VaktindeTheme(themePreference = theme) {
                VaktindeApp()
            }
        }
    }

    private fun applyLocale(language: String) {
        val locale = Locale(language)
        val currentLocale = resources.configuration.locales[0]
        if (currentLocale.language != locale.language) {
            Locale.setDefault(locale)
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}
