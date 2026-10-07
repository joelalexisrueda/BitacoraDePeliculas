package com.example.bitacoradepeliculas.domain.util

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DatePickerUtilsTest {

    @Test
    fun localDateToUtcMillis_and_back_isConsistent() {
        val date = LocalDate(2026, 6, 15)
        val millis = DatePickerUtils.localDateToUtcMillis(date)
        val convertedBack = DatePickerUtils.utcMillisToLocalDate(millis)
        assertEquals(date, convertedBack)
    }

    @Test
    fun edgeCases_utcConversion() {
        // Leap year
        val leapDate = LocalDate(2024, 2, 29)
        assertEquals(leapDate, DatePickerUtils.utcMillisToLocalDate(DatePickerUtils.localDateToUtcMillis(leapDate)))

        // First day of year
        val firstDay = LocalDate(2026, 1, 1)
        assertEquals(firstDay, DatePickerUtils.utcMillisToLocalDate(DatePickerUtils.localDateToUtcMillis(firstDay)))

        // Last day of year
        val lastDay = LocalDate(2026, 12, 31)
        assertEquals(lastDay, DatePickerUtils.utcMillisToLocalDate(DatePickerUtils.localDateToUtcMillis(lastDay)))
    }
}
