package com.example.bitacoradepeliculas.presentation.home

import androidx.compose.runtime.Immutable
import com.example.bitacoradepeliculas.data.model.MovieLog

@Immutable
sealed interface ReviewsState {
    data object Loading : ReviewsState
    data class Content(val reviews: List<MovieLog>) : ReviewsState
    data object Empty : ReviewsState
    data class Error(val message: String) : ReviewsState
}

@Immutable
data class HomeUiState(
    val userName: String = "Usuario",
    val reviewsState: ReviewsState = ReviewsState.Loading,
    val isRefreshing: Boolean = false,
    val isLoggingOut: Boolean = false
)
