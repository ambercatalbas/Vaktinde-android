package com.ambercatalbas.vaktinde.core.domain.model

enum class CalcMethod(
    val key: String,
    val aladhanId: Int,
    val fajrAngle: Double,
    val ishaAngle: Double,
    val ishaMinutes: Int = 0,
) {
    DIYANET("diyanet", 13, 18.0, 17.0),
    MWL("mwl", 3, 18.0, 17.0),
    ISNA("isna", 2, 15.0, 15.0),
    EGYPT("egypt", 5, 19.5, 17.5),
    UMM_AL_QURA("ummulqura", 4, 18.5, 0.0, ishaMinutes = 90),
    KARACHI("karachi", 1, 18.0, 18.0);

    companion object {
        fun fromKey(key: String): CalcMethod = entries.find { it.key == key } ?: DIYANET
    }
}
