package com.example.bitacoradepeliculas.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReviewDisplayTest {

    @Test
    fun formatReviewText_withValidText_returnsIt() {
        val text = "Una obra maestra absoluta."
        assertEquals(text, ReviewDisplayUtils.formatReviewText(text))
    }

    @Test
    fun formatReviewText_withNullOrBlank_returnsNull() {
        assertNull(ReviewDisplayUtils.formatReviewText(null))
        assertNull(ReviewDisplayUtils.formatReviewText(""))
        assertNull(ReviewDisplayUtils.formatReviewText("   "))
        assertNull(ReviewDisplayUtils.formatReviewText("\n\t "))
    }
}
