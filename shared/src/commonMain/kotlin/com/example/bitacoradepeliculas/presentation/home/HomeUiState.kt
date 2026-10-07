package com.example.bitacoradepeliculas.presentation.home

import androidx.compose.runtime.Immutable
import com.example.bitacoradepeliculas.data.model.MovieLog

@Immutable
sealed interface MovieLogsState {
    data object Loading : MovieLogsState
    data class Content(val movieLogs: List<MovieLog>) : MovieLogsState
    data object Empty : MovieLogsState
    data class Error(val message: String) : MovieLogsState
}

@Immutable
data class HomeUiState(
    val userName: String = "Usuario",
    val movieLogsState: MovieLogsState = MovieLogsState.Loading,
    val isRefreshing: Boolean = false,
    val isLoggingOut: Boolean = false
)
