package com.ambercatalbas.vaktinde.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.datastore.preferences.core.Preferences

class PrayerTimeWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<Preferences>()
        val cityName = prefs[CITY_NAME_KEY] ?: "İstanbul"
        val nextPrayerName = prefs[NEXT_PRAYER_NAME_KEY] ?: "Öğle"
        val nextPrayerTime = prefs[NEXT_PRAYER_TIME_KEY] ?: "12:30"
        val countdown = prefs[COUNTDOWN_KEY] ?: "02:34"
        val imsak = prefs[IMSAK_KEY] ?: "--:--"
        val gunes = prefs[GUNES_KEY] ?: "--:--"
        val ogle = prefs[OGLE_KEY] ?: "--:--"
        val ikindi = prefs[IKINDI_KEY] ?: "--:--"
        val aksam = prefs[AKSAM_KEY] ?: "--:--"
        val yatsi = prefs[YATSI_KEY] ?: "--:--"

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(WidgetColors.Background)
                .clickable(actionStartActivity<WidgetLaunchActivity>())
                .padding(16.dp),
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                // Header: City + Next prayer
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = cityName,
                        style = TextStyle(
                            color = ColorProvider(WidgetColors.TextDim),
                            fontSize = 12.sp,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = countdown,
                        style = TextStyle(
                            color = ColorProvider(WidgetColors.Gold),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Next prayer highlight
                Text(
                    text = nextPrayerName,
                    style = TextStyle(
                        color = ColorProvider(WidgetColors.TextLight),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = nextPrayerTime,
                    style = TextStyle(
                        color = ColorProvider(WidgetColors.Gold),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )

                Spacer(modifier = GlanceModifier.height(12.dp))

                // All prayer times row
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    PrayerTimeCell("İmsak", imsak, GlanceModifier.defaultWeight())
                    PrayerTimeCell("Güneş", gunes, GlanceModifier.defaultWeight())
                    PrayerTimeCell("Öğle", ogle, GlanceModifier.defaultWeight())
                    PrayerTimeCell("İkindi", ikindi, GlanceModifier.defaultWeight())
                    PrayerTimeCell("Akşam", aksam, GlanceModifier.defaultWeight())
                    PrayerTimeCell("Yatsı", yatsi, GlanceModifier.defaultWeight())
                }
            }
        }
    }

    @Composable
    private fun PrayerTimeCell(label: String, time: String, modifier: GlanceModifier) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(WidgetColors.TextDim),
                    fontSize = 9.sp,
                ),
            )
            Text(
                text = time,
                style = TextStyle(
                    color = ColorProvider(WidgetColors.TextLight),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }

    companion object {
        val CITY_NAME_KEY = stringPreferencesKey("widget_city_name")
        val NEXT_PRAYER_NAME_KEY = stringPreferencesKey("widget_next_prayer_name")
        val NEXT_PRAYER_TIME_KEY = stringPreferencesKey("widget_next_prayer_time")
        val COUNTDOWN_KEY = stringPreferencesKey("widget_countdown")
        val IMSAK_KEY = stringPreferencesKey("widget_imsak")
        val GUNES_KEY = stringPreferencesKey("widget_gunes")
        val OGLE_KEY = stringPreferencesKey("widget_ogle")
        val IKINDI_KEY = stringPreferencesKey("widget_ikindi")
        val AKSAM_KEY = stringPreferencesKey("widget_aksam")
        val YATSI_KEY = stringPreferencesKey("widget_yatsi")
    }
}

private object WidgetColors {
    val Background = android.graphics.Color.parseColor("#0A1A2E").let { android.graphics.Color.valueOf(it).toArgb() }.let { androidx.compose.ui.graphics.Color(it) }
    val TextLight = androidx.compose.ui.graphics.Color(0xFFEEF4FB)
    val TextDim = androidx.compose.ui.graphics.Color(0xFFDCE8F6).copy(alpha = 0.6f)
    val Gold = androidx.compose.ui.graphics.Color(0xFFD4A44A)
}
