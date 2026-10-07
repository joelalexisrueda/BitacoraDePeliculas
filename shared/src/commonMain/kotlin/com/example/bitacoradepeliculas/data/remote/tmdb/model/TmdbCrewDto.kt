package com.example.bitacoradepeliculas.data.remote.tmdb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbCrewDto(
    @SerialName("name") val name: String? = null,
    @SerialName("job") val job: String? = null
)
