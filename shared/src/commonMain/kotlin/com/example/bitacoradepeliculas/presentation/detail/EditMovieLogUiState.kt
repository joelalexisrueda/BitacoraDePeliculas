package com.example.bitacoradepeliculas.presentation.detail

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate

@Immutable
sealed interface EditMovieLogState {
    data object Loading : EditMovieLogState
    data class Editing(
        val movieTitle: String,
        val movieYear: Int?,
        val moviePosterPath: String?
    ) : EditMovieLogState
    data object NotFound : EditMovieLogState
    data class Error(val message: String) : EditMovieLogState
}

@Immutable
data class EditMovieLogUiState(
    val editState: EditMovieLogState = EditMovieLogState.Loading,
    val score: Double = 10.0,
    val logDate: LocalDate = LocalDate(2026, 1, 1),
    val reviewText: String = "",
    val originalScore: Double = 10.0,
    val originalLogDate: LocalDate = LocalDate(2026, 1, 1),
    val originalReviewText: String? = null,
    val isSaving: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val isDiscardDialogVisible: Boolean = false
) {
    val charCount: Int get() = reviewText.length

    val isDirty: Boolean get() {
        val scoreChanged = score != originalScore
        val dateChanged = logDate != originalLogDate
        val reviewChanged = !com.example.bitacoradepeliculas.domain.util.MovieLogFormValidator.isReviewEqual(originalReviewText, reviewText)
        return scoreChanged || dateChanged || reviewChanged
    }
}
