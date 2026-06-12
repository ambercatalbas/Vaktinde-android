package com.ambercatalbas.vaktinde.core.domain.model

data class AdhanSound(
    val id: String,
    val fileName: String,
    val fullFileName: String? = null,
    val isBuiltIn: Boolean = true,
) {
    companion object {
        val DEFAULT_SOUNDS = listOf(
            AdhanSound("sabah_ezan", "sabah_ezan", "sabah_ezan_full"),
            AdhanSound("ogle_ezan", "ogle_ezan", "ogle_ezan_full"),
            AdhanSound("ikindi_ezan", "ikindi_ezan", "ikindi_ezan_full"),
            AdhanSound("aksam_ezan", "aksam_ezan", "aksam_ezan_full"),
            AdhanSound("yatsi_ezan", "yatsi_ezan", "yatsi_ezan_full"),
            AdhanSound("sela", "sela", null),
        )

        val DEFAULT = DEFAULT_SOUNDS.first()
        val SELA = DEFAULT_SOUNDS.last()

        fun fromId(id: String): AdhanSound =
            DEFAULT_SOUNDS.find { it.id == id } ?: DEFAULT
    }
}
