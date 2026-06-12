package com.ambercatalbas.vaktinde.core.domain.model

sealed class PrayerTimeError : Exception() {
    data object InvalidUrl : PrayerTimeError()
    data class ApiError(val statusCode: Int) : PrayerTimeError()
    data object NoData : PrayerTimeError()
    data object DecodingError : PrayerTimeError()
    data object NoCache : PrayerTimeError()
}
