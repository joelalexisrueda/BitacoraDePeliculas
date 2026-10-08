package com.example.bitacoradepeliculas

import com.example.bitacoradepeliculas.data.model.MovieLog
import com.example.bitacoradepeliculas.domain.model.DirectorState
import com.example.bitacoradepeliculas.domain.model.MovieSearchResult
import kotlinx.datetime.LocalDate

fun createTestMovieLog(
    id: Long = 1L,
    movieTitle: String = "Test Movie",
    movieYear: Int? = 2024,
    moviePosterPath: String? = "/poster.jpg",
    score: Double = 8.0,
    logDate: LocalDate = LocalDate(2026, 6, 15),
    reviewText: String? = "Buena película"
): MovieLog = MovieLog(
    id = id,
    movieTitle = movieTitle,
    movieYear = movieYear,
    moviePosterPath = moviePosterPath,
    score = score,
    logDate = logDate,
    reviewText = reviewText
)

fun createTestMovieSearchResult(
    tmdbId: Long = 100L,
    title: String = "Inception",
    year: Int? = 2010,
    posterPath: String? = "/poster.jpg",
    directorState: DirectorState = DirectorState.Loading
): MovieSearchResult = MovieSearchResult(
    tmdbId = tmdbId,
    title = title,
    year = year,
    posterPath = posterPath,
    directorState = directorState
)
