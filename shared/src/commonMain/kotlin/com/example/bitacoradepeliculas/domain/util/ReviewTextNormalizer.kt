package com.example.bitacoradepeliculas.domain.util

object ReviewTextNormalizer {
    const val MAX_LENGTH = 1000

    /**
     * Normaliza el texto de la reseña:
     * - Recorta espacios iniciales/finales.
     * - Limita el texto a un máximo de 1000 caracteres.
     * - Si queda vacío o en blanco, retorna null.
     */
    fun normalize(text: String): String? {
        val trimmed = text.trim().take(MAX_LENGTH)
        return if (trimmed.isBlank()) null else trimmed
    }
}
