package com.example.bitacoradepeliculas.domain.util

import kotlinx.datetime.LocalDate

object MovieLogFormValidator {
    fun validateDate(newDate: LocalDate, today: LocalDate): LocalDate {
        return if (newDate > today) today else newDate
    }

    fun validateReviewText(newText: String): String {
        return newText.take(ReviewTextNormalizer.MAX_LENGTH)
    }

    fun isReviewEqual(original: String?, current: String?): Boolean {
        val normOrig = ReviewTextNormalizer.normalize(original ?: "")
        val normCurr = ReviewTextNormalizer.normalize(current ?: "")
        return normOrig == normCurr
    }
}
