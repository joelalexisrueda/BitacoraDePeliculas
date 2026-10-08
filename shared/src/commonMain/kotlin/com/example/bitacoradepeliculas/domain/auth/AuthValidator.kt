package com.example.bitacoradepeliculas.domain.auth

/**
 * Funciones puras de validación de campos de autenticación.
 */
object AuthValidator {
    const val NAME_MAX_LENGTH = 15
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> "El correo electrónico es requerido."
            !EMAIL_REGEX.matches(trimmed) -> "Ingresa un correo electrónico válido."
            else -> null
        }
    }

    /**
     * Recorta un nombre a un máximo de [maxLength] caracteres (UTF-16 code units),
     * asegurándose de no cortar un par suplente (surrogate pair, como emojis o caracteres especiales fuera del plano básico) por la mitad.
     * Si el índice de corte cae exactamente después de un high surrogate sin su low surrogate correspondiente,
     * se descarta ese carácter completo retrocediendo una posición.
     */
    fun truncateName(text: String, maxLength: Int = NAME_MAX_LENGTH): String {
        if (text.length <= maxLength) return text
        var index = maxLength
        if (index in 1 until text.length) {
            val prevChar = text[index - 1]
            if (prevChar.isHighSurrogate()) {
                index -= 1
            }
        }
        return text.take(index)
    }

    fun validateName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "El nombre es requerido."
            trimmed.length > NAME_MAX_LENGTH -> "El nombre no puede tener más de 15 caracteres."
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
