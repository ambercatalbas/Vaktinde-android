package com.ambercatalbas.vaktinde.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun logScreenView(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
    }

    fun logPrayerNotificationEnabled(prayerName: String) {
        firebaseAnalytics.logEvent("prayer_notification_enabled") {
            param("prayer_name", prayerName)
        }
    }

    fun logCitySelected(cityName: String) {
        firebaseAnalytics.logEvent("city_selected") {
            param("city_name", cityName)
        }
    }

    fun logCalcMethodChanged(method: String) {
        firebaseAnalytics.logEvent("calc_method_changed") {
            param("method", method)
        }
    }

    fun logThemeChanged(theme: String) {
        firebaseAnalytics.logEvent("theme_changed") {
            param("theme", theme)
        }
    }

    fun logLanguageChanged(language: String) {
        firebaseAnalytics.logEvent("language_changed") {
            param("language", language)
        }
    }

    fun logOnboardingCompleted() {
        firebaseAnalytics.logEvent("onboarding_completed") {}
    }

    fun logQiblaViewed() {
        firebaseAnalytics.logEvent("qibla_viewed") {}
    }

    fun logWidgetAdded() {
        firebaseAnalytics.logEvent("widget_added") {}
    }
}
