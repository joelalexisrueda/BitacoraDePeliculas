package com.example.bitacoradepeliculas.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object SearchMovie : Screen

    @Serializable
    data class LogMovie(val title: String, val year: Int? = null, val posterPath: String? = null) : Screen

    @Serializable
    data class MovieLogDetail(val movieLogId: Long) : Screen
}
