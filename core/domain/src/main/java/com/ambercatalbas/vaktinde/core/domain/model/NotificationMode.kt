package com.ambercatalbas.vaktinde.core.domain.model

enum class NotificationMode(val key: String) {
    ADHAN("adhan"),
    SILENT("silent"),
    OFF("off");

    companion object {
        fun fromKey(key: String): NotificationMode =
            entries.find { it.key == key } ?: ADHAN
    }
}
