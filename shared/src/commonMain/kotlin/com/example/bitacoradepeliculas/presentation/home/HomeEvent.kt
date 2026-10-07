package com.example.bitacoradepeliculas.presentation.home

sealed interface HomeEvent {
    data class ShowSnackbar(val message: String) : HomeEvent
    data object NavigateToLogin : HomeEvent
    data object NavigateToSearchMovie : HomeEvent
    data class NavigateToReviewDetail(val reviewId: Long) : HomeEvent
}
