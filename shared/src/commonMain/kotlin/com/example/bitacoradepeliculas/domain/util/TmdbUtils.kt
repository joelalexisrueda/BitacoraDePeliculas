package com.example.bitacoradepeliculas.domain.util

object TmdbUtils {
    const val DEFAULT_POSTER_SIZE = "w342"
    const val LIST_POSTER_SIZE = "w185"

    fun getPosterUrl(posterPath: String?, size: String = DEFAULT_POSTER_SIZE): String? {
        if (posterPath.isNullOrBlank()) return null
        val cleanPath = if (posterPath.startsWith("/")) posterPath else "/$posterPath"
        return "https://image.tmdb.org/t/p/$size$cleanPath"
    }
}
