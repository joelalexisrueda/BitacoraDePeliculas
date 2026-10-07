package com.example.bitacoradepeliculas.domain.util

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DateFormatterTest {

    @Test
    fun formatToSpanish_singleDigitDay_formatsCorrectly() {
        val date = LocalDate(2026, 10, 6)
        val formatted = DateFormatter.formatToSpanish(date)
        assertEquals("6 oct 2026", formatted)
    }

    @Test
    fun formatToSpanish_multipleMonths_formatsCorrectly() {
        assertEquals("1 ene 2025", DateFormatter.formatToSpanish(LocalDate(2025, 1, 1)))
        assertEquals("15 feb 2025", DateFormatter.formatToSpanish(LocalDate(2025, 2, 15)))
        assertEquals("31 dic 2024", DateFormatter.formatToSpanish(LocalDate(2024, 12, 31)))
    }
}
