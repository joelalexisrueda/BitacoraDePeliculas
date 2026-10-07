package com.example.bitacoradepeliculas.domain.util

import kotlinx.datetime.LocalDate

object DateFormatter {
    private val SPANISH_MONTHS = listOf(
        "ene", "feb", "mar", "abr", "may", "jun",
        "jul", "ago", "sep", "oct", "nov", "dic"
    )

    fun formatToSpanish(date: LocalDate): String {
        @Suppress("DEPRECATION")
        val day = date.dayOfMonth
        val month = SPANISH_MONTHS[date.month.ordinal]
        val year = date.year
        return "$day $month $year"
    }
}
