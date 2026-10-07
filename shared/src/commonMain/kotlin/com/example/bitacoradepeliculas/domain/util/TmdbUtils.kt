package com.example.bitacoradepeliculas.domain.util

object TmdbUtils {
    const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342"

    fun getPosterUrl(posterPath: String?): String? {
        if (posterPath.isNullOrBlank()) return null
        val cleanPath = if (posterPath.startsWith("/")) posterPath else "/$posterPath"
        return "$POSTER_BASE_URL$cleanPath"
    }
}
