package com.example.bitacoradepeliculas.presentation.search

import androidx.compose.runtime.Immutable
import com.example.bitacoradepeliculas.domain.model.MovieSearchResult

@Immutable
sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data class Results(val items: List<MovieSearchResult>) : SearchState
    data object Empty : SearchState
    data class Error(val message: String) : SearchState
}

@Immutable
data class SearchMovieUiState(
    val query: String = "",
    val searchState: SearchState = SearchState.Idle
)
