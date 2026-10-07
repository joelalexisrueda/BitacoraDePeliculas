package com.example.bitacoradepeliculas.domain.util

object RatingUtils {

    /**
     * Calcula la puntuación (de 0.0 a 10.0 en pasos de 0.5) según la estrella tocada (1..10)
     * y la mitad seleccionada (izquierda o derecha).
     *
     * REGLA DEL CERO: Si se toca la mitad izquierda de la primera estrella cuando el puntaje
     * actual ya es 0.5, se reinicia la puntuación a 0.0.
     */
    fun calculateScore(starIndex: Int, isLeftHalf: Boolean, currentScore: Double): Double {
        require(starIndex in 1..10) { "El índice de estrella debe estar entre 1 y 10" }

        if (starIndex == 1 && isLeftHalf && currentScore == 0.5) {
            return 0.0
        }

        return if (isLeftHalf) {
            starIndex - 0.5
        } else {
            starIndex.toDouble()
        }
    }

    /**
     * Formatea el valor de puntuación para mostrarlo en pantalla (ej: "8.5 / 10", "10 / 10", "0 / 10").
     */
    fun formatScoreText(score: Double): String {
        val scoreFormatted = if (score % 1.0 == 0.0) {
            score.toInt().toString()
        } else {
            score.toString()
        }
        return "$scoreFormatted / 10"
    }
}
