package com.example.bitacoradepeliculas.domain.util

object ReviewDisplayUtils {
    fun formatReviewText(reviewText: String?): String? {
        return if (reviewText.isNullOrBlank()) null else reviewText
    }
}
