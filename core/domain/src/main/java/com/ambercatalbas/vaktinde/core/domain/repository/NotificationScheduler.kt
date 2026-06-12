package com.ambercatalbas.vaktinde.core.domain.repository

import com.ambercatalbas.vaktinde.core.domain.model.Prayer

interface NotificationScheduler {
    fun scheduleToday(prayers: List<Prayer>)
}
