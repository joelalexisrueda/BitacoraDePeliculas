package com.example.bitacoradepeliculas.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReviewTextNormalizerTest {

    @Test
    fun normalize_withValidText_trimsAndReturns() {
        val text = "   Excelente película. Muy recomendada.   "
        val result = ReviewTextNormalizer.normalize(text)
        assertEquals("Excelente película. Muy recomendada.", result)
    }

    @Test
    fun normalize_withBlankOrEmpty_returnsNull() {
        assertNull(ReviewTextNormalizer.normalize(""))
        assertNull(ReviewTextNormalizer.normalize("   "))
        assertNull(ReviewTextNormalizer.normalize("\n\t"))
    }

    @Test
    fun normalize_exceedingMaxLength_truncates() {
        val longText = "A".repeat(1200)
        val result = ReviewTextNormalizer.normalize(longText)
        assertEquals(ReviewTextNormalizer.MAX_LENGTH, result?.length)
    }
}
