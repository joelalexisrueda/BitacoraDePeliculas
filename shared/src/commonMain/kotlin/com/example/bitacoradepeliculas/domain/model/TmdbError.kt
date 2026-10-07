package com.example.bitacoradepeliculas.domain.model

import io.ktor.client.plugins.ResponseException

sealed class TmdbError(val userMessage: String) : Exception(userMessage) {
    data object MissingApiKey : TmdbError("Falta configurar la clave de TMDB.")
    data object NoInternet : TmdbError("Sin conexión a internet. Revisa tu red.")
    data object Timeout : TmdbError("La solicitud ha superado el tiempo de espera. Intenta de nuevo.")
    data object InvalidApiKey : TmdbError("Clave de API de TMDB no válida.")
    data object RateLimited : TmdbError("Límite de solicitudes superado. Intenta en unos momentos.")
    data object ServerError : TmdbError("Error en los servidores de TMDB. Intenta más tarde.")
    data class Unknown(val rawMessage: String?) : TmdbError(rawMessage ?: "Ocurrió un error inesperado al buscar en TMDB.")
}

fun Throwable.toTmdbError(): TmdbError {
    if (this is TmdbError) return this
    if (this is ResponseException) {
        return when (response.status.value) {
            401 -> TmdbError.InvalidApiKey
            429 -> TmdbError.RateLimited
            in 500..599 -> TmdbError.ServerError
            else -> TmdbError.Unknown("Error HTTP ${response.status.value}")
        }
    }
    val msg = message?.lowercase() ?: ""
    return when {
        msg.contains("401") || msg.contains("invalid api key") -> TmdbError.InvalidApiKey
        msg.contains("429") || msg.contains("rate limit") -> TmdbError.RateLimited
        msg.contains("500") || msg.contains("502") || msg.contains("503") || msg.contains("504") -> TmdbError.ServerError
        msg.contains("timeout") || msg.contains("sockettimeout") -> TmdbError.Timeout
        msg.contains("unable to resolve host") || msg.contains("failed to connect") || msg.contains("network") || msg.contains("connectexception") -> TmdbError.NoInternet
        else -> TmdbError.Unknown(message)
    }
}
