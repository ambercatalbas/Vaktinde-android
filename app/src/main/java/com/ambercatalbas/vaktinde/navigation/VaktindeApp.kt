package com.ambercatalbas.vaktinde.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ambercatalbas.vaktinde.feature.calendar.CalendarScreen
import com.ambercatalbas.vaktinde.feature.home.HomeScreen
import com.ambercatalbas.vaktinde.feature.onboarding.OnboardingScreen
import com.ambercatalbas.vaktinde.feature.onboarding.OnboardingViewModel
import com.ambercatalbas.vaktinde.feature.qibla.QiblaScreen
import com.ambercatalbas.vaktinde.feature.settings.SettingsScreen
import com.ambercatalbas.vaktinde.feature.settings.city.CitySelectionScreen

@Composable
fun VaktindeApp() {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val hasCompletedOnboarding by onboardingViewModel.hasCompletedOnboarding.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Route.Home.path, Route.Qibla.path, Route.Calendar.path, Route.Settings.path
    )

    val startDestination = if (hasCompletedOnboarding) Route.Home.path else Route.Onboarding.path

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                VaktindeBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Onboarding.path) {
                OnboardingScreen(
                    onComplete = {
                        onboardingViewModel.completeOnboarding()
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Onboarding.path) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.Home.path) {
                HomeScreen()
            }
            composable(Route.Qibla.path) {
                QiblaScreen()
            }
            composable(Route.Calendar.path) {
                CalendarScreen()
            }
            composable(Route.Settings.path) {
                SettingsScreen(
                    onNavigateToCitySelection = {
                        navController.navigate(Route.CitySelection.path)
                    }
                )
            }
            composable(Route.CitySelection.path) {
                CitySelectionScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
