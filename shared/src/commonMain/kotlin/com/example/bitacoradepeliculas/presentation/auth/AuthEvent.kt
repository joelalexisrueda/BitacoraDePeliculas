package com.example.bitacoradepeliculas.presentation.auth

/**
 * Eventos únicos (one-off) emitidos por los ViewModels de autenticación.
 */
sealed interface AuthEvent {
    data object Success : AuthEvent
}
