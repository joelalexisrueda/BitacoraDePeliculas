package com.example.bitacoradepeliculas.domain.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

interface TodayProvider {
    fun today(): LocalDate
}

class SystemTodayProvider : TodayProvider {
    override fun today(): LocalDate {
        @Suppress("DEPRECATION")
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
}
