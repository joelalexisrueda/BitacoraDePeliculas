package com.example.bitacoradepeliculas.presentation.detail

sealed interface MovieLogDetailEvent {
    data object NavigateBack : MovieLogDetailEvent
    data class NavigateToEdit(val movieLogId: Long) : MovieLogDetailEvent
    data object NavigateToHome : MovieLogDetailEvent
    data object NavigateToLogin : MovieLogDetailEvent
    data class ShowSnackbar(val message: String) : MovieLogDetailEvent
}
