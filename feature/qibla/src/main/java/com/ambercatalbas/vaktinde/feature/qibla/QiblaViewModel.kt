package com.ambercatalbas.vaktinde.feature.qibla

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ambercatalbas.vaktinde.core.domain.model.City
import com.ambercatalbas.vaktinde.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class QiblaUiState(
    val heading: Float = 0f,
    val qiblaBearing: Double = 151.5,
    val isAligned: Boolean = false,
    val cityName: String = "İstanbul",
    val distanceKm: String = "0 km",
    val bearingText: String = "152°",
)

private const val KAABA_LAT = 21.4225
private const val KAABA_LNG = 39.8262
private const val ALIGNMENT_THRESHOLD = 6.0

@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel(), SensorEventListener {

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    private val selectedCity = userPreferencesRepository.selectedCity
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), City.default)

    private var sensorManager: SensorManager? = null
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    init {
        viewModelScope.launch {
            selectedCity.collect { city ->
                val bearing = calculateQiblaBearing(city.latitude, city.longitude)
                val distance = calculateDistance(city.latitude, city.longitude)
                _uiState.update {
                    it.copy(
                        cityName = city.name,
                        qiblaBearing = bearing,
                        distanceKm = "%.0f km".format(distance),
                        bearingText = "${bearing.roundToInt()}°",
                    )
                }
            }
        }
    }

    fun startSensor(sensorManager: SensorManager) {
        this.sensorManager = sensorManager
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopSensor() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            val heading = (azimuth + 360) % 360

            val bearing = _uiState.value.qiblaBearing
            val diff = ((bearing - heading + 180) % 360 + 360) % 360 - 180
            val aligned = abs(diff) < ALIGNMENT_THRESHOLD

            _uiState.update {
                it.copy(heading = heading, isAligned = aligned)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun calculateQiblaBearing(lat: Double, lng: Double): Double {
        val lat1 = Math.toRadians(lat)
        val lng1 = Math.toRadians(lng)
        val lat2 = Math.toRadians(KAABA_LAT)
        val lng2 = Math.toRadians(KAABA_LNG)

        val dLng = lng2 - lng1
        val y = sin(dLng) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLng)
        val bearing = Math.toDegrees(atan2(y, x))
        return (bearing + 360) % 360
    }

    private fun calculateDistance(lat: Double, lng: Double): Double {
        val r = 6371.0
        val lat1 = Math.toRadians(lat)
        val lat2 = Math.toRadians(KAABA_LAT)
        val dLat = lat2 - lat1
        val dLng = Math.toRadians(KAABA_LNG - lng)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) * sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
