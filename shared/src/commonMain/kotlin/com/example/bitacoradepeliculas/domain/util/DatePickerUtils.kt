package com.example.bitacoradepeliculas.domain.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

/**
 * Utilidades de conversión entre [LocalDate] y milisegundos en UTC.
 *
 * NOTA DE ARQUITECTURA / ZONA HORARIA:
 * El [DatePickerState] de Material 3 opera estrictamente con marcas de tiempo a medianoche en UTC.
 * Para evitar desplazamientos de fecha ("off-by-one day") causados por la zona horaria local del dispositivo,
 * la conversión de y hacia milisegundos se realiza de forma pura usando [TimeZone.UTC].
 */
object DatePickerUtils {

    @Suppress("DEPRECATION")
    fun localDateToUtcMillis(date: LocalDate): Long {
        return date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }

    @Suppress("DEPRECATION")
    fun utcMillisToLocalDate(utcMillis: Long): LocalDate {
        return Instant.fromEpochMilliseconds(utcMillis)
            .toLocalDateTime(TimeZone.UTC).date
    }
}
