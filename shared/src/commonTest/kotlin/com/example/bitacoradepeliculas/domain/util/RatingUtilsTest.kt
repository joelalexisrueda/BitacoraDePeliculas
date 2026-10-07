package com.example.bitacoradepeliculas.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals

class RatingUtilsTest {

    @Test
    fun calculateScore_tapsLeftAndRightHalves() {
        // Star 1 left half -> 0.5
        assertEquals(0.5, RatingUtils.calculateScore(1, true, 10.0))
        // Star 1 left half when score is 0.5 -> 0.0 (Zero rule)
        assertEquals(0.0, RatingUtils.calculateScore(1, true, 0.5))
        // Star 1 right half -> 1.0
        assertEquals(1.0, RatingUtils.calculateScore(1, false, 0.0))
        // Star 8 left half -> 7.5
        assertEquals(7.5, RatingUtils.calculateScore(8, true, 10.0))
        // Star 10 right half -> 10.0
        assertEquals(10.0, RatingUtils.calculateScore(10, false, 5.0))
    }

    @Test
    fun formatScoreText_formatsCorrectly() {
        assertEquals("10 / 10", RatingUtils.formatScoreText(10.0))
        assertEquals("8.5 / 10", RatingUtils.formatScoreText(8.5))
        assertEquals("0 / 10", RatingUtils.formatScoreText(0.0))
    }
}
