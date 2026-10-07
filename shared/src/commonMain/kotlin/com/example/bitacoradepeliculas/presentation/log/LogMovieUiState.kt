package com.example.bitacoradepeliculas.presentation.log

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
data class LogMovieUiState(
    val movieTitle: String = "",
    val movieYear: Int? = null,
    val moviePosterPath: String? = null,
    val score: Double = 10.0,
    val logDate: LocalDate,
    val reviewText: String = "",
    val isSaving: Boolean = false,
    val isDatePickerVisible: Boolean = false
) {
    val charCount: Int get() = reviewText.length
}
