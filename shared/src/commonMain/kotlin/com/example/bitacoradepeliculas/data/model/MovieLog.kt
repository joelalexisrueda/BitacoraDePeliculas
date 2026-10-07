package com.example.bitacoradepeliculas.data.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MovieLog(
    val id: Long = 0L,
    @SerialName("user_id") val userId: String = "",
    @SerialName("movie_title") val movieTitle: String,
    @SerialName("movie_year") val movieYear: Int? = null,
    @SerialName("movie_poster_path") val moviePosterPath: String? = null,
    val score: Double,
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("log_date") val reviewDate: LocalDate,
    @SerialName("review_text") val reviewText: String? = null
)
