package com.example.bitacoradepeliculas.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TmdbUtilsTest {

    @Test
    fun getPosterUrl_withNullOrBlank_returnsNull() {
        assertNull(TmdbUtils.getPosterUrl(null))
        assertNull(TmdbUtils.getPosterUrl(""))
        assertNull(TmdbUtils.getPosterUrl("   "))
    }

    @Test
    fun getPosterUrl_withLeadingSlash_returnsFullUrl() {
        val url = TmdbUtils.getPosterUrl("/poster.jpg")
        assertEquals("https://image.tmdb.org/t/p/w342/poster.jpg", url)
    }

    @Test
    fun getPosterUrl_withoutLeadingSlash_returnsFullUrl() {
        val url = TmdbUtils.getPosterUrl("poster.jpg")
        assertEquals("https://image.tmdb.org/t/p/w342/poster.jpg", url)
    }
}
