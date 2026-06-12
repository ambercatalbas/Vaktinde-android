package com.ambercatalbas.vaktinde.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Qibla : Route("qibla")
    data object Calendar : Route("calendar")
    data object Settings : Route("settings")
    data object Onboarding : Route("onboarding")
    data object CitySelection : Route("city_selection")
}
