package com.example.bitacoradepeliculas.presentation.detail

sealed interface EditMovieLogEvent {
    data object NavigateBack : EditMovieLogEvent
    data object NavigateToHome : EditMovieLogEvent
    data object NavigateToLogin : EditMovieLogEvent
    data class ShowSnackbar(val message: String) : EditMovieLogEvent
}
