package com.ambercatalbas.vaktinde.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ambercatalbas.vaktinde.feature.calendar.CalendarScreen
import com.ambercatalbas.vaktinde.feature.home.HomeScreen
import com.ambercatalbas.vaktinde.feature.qibla.QiblaScreen
import com.ambercatalbas.vaktinde.feature.settings.SettingsScreen

@Composable
fun VaktindeApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.path,
            modifier = Modifier.padding(innerPadding)
        ) {
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
                SettingsScreen()
            }
        }
    }
}
