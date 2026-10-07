package com.example.bitacoradepeliculas.domain.model

sealed class DataError(val userMessage: String) : Exception(userMessage) {
    data object NoInternet : DataError("Sin conexión a internet. Intenta de nuevo.")
    data object SessionExpired : DataError("Tu sesión ha expirado. Inicia sesión nuevamente.")
    data class Unknown(val rawMessage: String?) : DataError(rawMessage ?: "Ocurrió un error inesperado al cargar los datos.")
}

fun Throwable.toDataError(): DataError {
    if (this is DataError) return this
    val msg = message?.lowercase() ?: ""
    return when {
        msg.contains("jwt expired") || msg.contains("session_expired") || msg.contains("unauthorized") || msg.contains("401") -> DataError.SessionExpired
        msg.contains("unable to resolve host") || msg.contains("failed to connect") || msg.contains("network") || msg.contains("connectexception") -> DataError.NoInternet
        else -> DataError.Unknown(message)
    }
}
