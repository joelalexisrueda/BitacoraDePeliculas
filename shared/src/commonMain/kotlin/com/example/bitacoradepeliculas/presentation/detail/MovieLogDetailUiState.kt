package com.example.bitacoradepeliculas.presentation.detail

import androidx.compose.runtime.Immutable
import com.example.bitacoradepeliculas.data.model.MovieLog

@Immutable
sealed interface MovieLogDetailState {
    data object Loading : MovieLogDetailState
    data class Content(val movieLog: MovieLog) : MovieLogDetailState
    data object NotFound : MovieLogDetailState
    data class Error(val message: String) : MovieLogDetailState
}

@Immutable
data class MovieLogDetailUiState(
    val detailState: MovieLogDetailState = MovieLogDetailState.Loading,
    val isDeleteDialogVisible: Boolean = false,
    val isDeleting: Boolean = false
)
