package com.ambercatalbas.vaktinde.core.data.remote.diyanet

import com.google.gson.annotations.SerializedName

data class DiyanetResponse(
    val success: Boolean,
    val data: List<DiyanetDayItem>,
)

data class DiyanetDayItem(
    val date: String,
    val times: DiyanetTimes,
)

data class DiyanetTimes(
    val imsak: String,
    val gunes: String,
    val ogle: String,
    val ikindi: String,
    val aksam: String,
    val yatsi: String,
)

data class DiyanetLocationResponse(
    val success: Boolean,
    val data: List<DiyanetLocation>,
)

data class DiyanetLocation(
    @SerializedName("_id") val id: String,
    val name: String,
    @SerializedName("name_en") val nameEn: String?,
    @SerializedName("state_id") val stateId: String?,
)
