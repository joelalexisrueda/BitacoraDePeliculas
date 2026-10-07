package com.example.bitacoradepeliculas.presentation.log

sealed interface LogMovieEvent {
    data object NavigateBack : LogMovieEvent
    data object NavigateToHome : LogMovieEvent
    data object NavigateToLogin : LogMovieEvent
    data class ShowSnackbar(val message: String) : LogMovieEvent
}
