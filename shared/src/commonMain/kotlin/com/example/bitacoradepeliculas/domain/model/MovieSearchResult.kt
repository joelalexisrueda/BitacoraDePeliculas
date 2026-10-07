package com.example.bitacoradepeliculas.domain.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DirectorState {
    data object Loading : DirectorState
    data class Loaded(val name: String?) : DirectorState
    data object Failed : DirectorState
}

@Immutable
data class MovieSearchResult(
    val tmdbId: Long,
    val title: String,
    val year: Int?,
    val posterPath: String?,
    val directorState: DirectorState = DirectorState.Loading
)
