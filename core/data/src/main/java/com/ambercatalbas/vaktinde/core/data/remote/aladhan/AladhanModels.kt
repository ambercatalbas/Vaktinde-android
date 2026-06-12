package com.ambercatalbas.vaktinde.core.data.remote.aladhan

import com.google.gson.annotations.SerializedName

data class AladhanResponse(
    val code: Int,
    val status: String,
    val data: List<AladhanDay>,
)

data class AladhanDay(
    val timings: AladhanTimings,
    val date: AladhanDate,
)

data class AladhanTimings(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Sunrise") val sunrise: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String,
)

data class AladhanDate(
    val gregorian: AladhanGregorian,
    val hijri: AladhanHijri?,
)

data class AladhanGregorian(
    val date: String, // DD-MM-YYYY
    val day: String,
    val month: AladhanMonth,
    val year: String,
)

data class AladhanHijri(
    val date: String,
    val day: String,
    val month: AladhanHijriMonth?,
    val year: String,
)

data class AladhanHijriMonth(
    val number: Int,
    val en: String,
    val ar: String,
)

data class AladhanMonth(
    val number: Int,
)
