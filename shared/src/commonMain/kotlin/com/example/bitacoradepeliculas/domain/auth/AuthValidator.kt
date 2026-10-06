package com.example.bitacoradepeliculas.domain.auth

/**
 * Funciones puras de validación de campos de autenticación.
 */
object AuthValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> "El correo electrónico es requerido."
            !EMAIL_REGEX.matches(trimmed) -> "Ingresa un correo electrónico válido."
            else -> null
        }
    }

    fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "El nombre completo es requerido."
            trimmed.length < 2 -> "El nombre debe tener al menos 2 caracteres."
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "La contraseña es requerida."
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres."
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Confirma tu contraseña."
            password != confirmPassword -> "Las contraseñas no coinciden."
            else -> null
        }
    }
}
