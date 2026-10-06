package com.example.bitacoradepeliculas.domain.auth

/**
 * Representa los errores posibles durante el flujo de autenticación,
 * mapeados a mensajes claros y amigables en español.
 */
sealed class AuthError(val userMessage: String) : Exception(userMessage) {
    data object InvalidCredentials : AuthError("Credenciales inválidas. Revisa tu correo o contraseña.")
    data object EmailAlreadyRegistered : AuthError("El correo electrónico ya está registrado.")
    data object WeakPassword : AuthError("La contraseña debe tener al menos 6 caracteres.")
    data object NoInternet : AuthError("Sin conexión a internet. Intenta de nuevo.")
    data class Unknown(val rawMessage: String?) : AuthError(rawMessage ?: "Ocurrió un error inesperado.")
}

/**
 * Mapea cualquier [Throwable] a un [AuthError] con mensaje descriptivo en español.
 */
fun Throwable.toAuthError(): AuthError {
    if (this is AuthError) return this
    val msg = message?.lowercase() ?: ""
    return when {
        msg.contains("invalid login credentials") || msg.contains("invalid_credentials") -> AuthError.InvalidCredentials
        msg.contains("already registered") || msg.contains("user_already_exists") -> AuthError.EmailAlreadyRegistered
        msg.contains("weak password") || msg.contains("password should be") -> AuthError.WeakPassword
        msg.contains("unable to resolve host") || msg.contains("failed to connect") || msg.contains("network") || msg.contains("connectexception") -> AuthError.NoInternet
        else -> AuthError.Unknown(message)
    }
}
