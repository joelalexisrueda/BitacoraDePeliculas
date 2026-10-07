package com.example.bitacoradepeliculas.presentation.search

sealed interface SearchMovieEvent {
    data object NavigateBack : SearchMovieEvent
    data class NavigateToLogMovie(val title: String, val year: Int?, val posterPath: String?) : SearchMovieEvent
}
