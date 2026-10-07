package com.example.bitacoradepeliculas.data.remote.tmdb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbCreditsResponse(
    @SerialName("crew") val crew: List<TmdbCrewDto> = emptyList()
)
