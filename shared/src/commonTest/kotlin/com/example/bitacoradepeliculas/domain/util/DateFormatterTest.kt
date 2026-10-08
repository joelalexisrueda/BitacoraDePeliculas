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
    fun formatToSpanish_all12MonthsAndDays_formatsCorrectly() {
        assertEquals("1 ene 2025", DateFormatter.formatToSpanish(LocalDate(2025, 1, 1)))
        assertEquals("15 feb 2025", DateFormatter.formatToSpanish(LocalDate(2025, 2, 15)))
        assertEquals("20 mar 2025", DateFormatter.formatToSpanish(LocalDate(2025, 3, 20)))
        assertEquals("12 may 2025", DateFormatter.formatToSpanish(LocalDate(2025, 5, 12)))
        assertEquals("25 jun 2025", DateFormatter.formatToSpanish(LocalDate(2025, 6, 25)))
        assertEquals("18 ago 2025", DateFormatter.formatToSpanish(LocalDate(2025, 8, 18)))
        assertEquals("11 nov 2023", DateFormatter.formatToSpanish(LocalDate(2023, 11, 11)))
        assertEquals("31 dic 2024", DateFormatter.formatToSpanish(LocalDate(2024, 12, 31)))
    }
}
