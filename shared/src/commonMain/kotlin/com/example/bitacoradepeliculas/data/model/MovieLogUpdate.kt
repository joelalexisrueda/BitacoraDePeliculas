package com.example.bitacoradepeliculas.data.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class MovieLogUpdate(
    val score: Double,
    @Serializable(with = LocalDateSerializer::class)
    @SerialName("log_date") val logDate: LocalDate,
    @SerialName("review_text") val reviewText: String?
)
