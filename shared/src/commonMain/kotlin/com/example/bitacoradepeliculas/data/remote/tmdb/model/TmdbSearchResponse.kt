package com.example.bitacoradepeliculas.data.remote.tmdb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbSearchResponse(
    @SerialName("results") val results: List<TmdbMovieDto> = emptyList(),
    @SerialName("total_results") val totalResults: Int = 0
)
