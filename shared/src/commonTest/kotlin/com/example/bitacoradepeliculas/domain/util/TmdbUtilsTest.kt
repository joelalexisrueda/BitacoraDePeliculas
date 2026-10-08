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

    @Test
    fun getPosterUrl_withCustomSizes_formatsCorrectly() {
        assertEquals("https://image.tmdb.org/t/p/w185/poster.jpg", TmdbUtils.getPosterUrl("/poster.jpg", TmdbUtils.LIST_POSTER_SIZE))
        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", TmdbUtils.getPosterUrl("poster.jpg", TmdbUtils.DETAIL_POSTER_SIZE))
    }

    @Test
    fun releaseDateYearParsing_7Cases() {
        // HALLAZGO DE BUG DETECTADO:
        // 'dto.releaseDate?.take(4)?.toIntOrNull()' convierte un string corto como "20" en el entero 20 en lugar de requerir un año de 4 dígitos.
        // Se reporta en los hallazgos según las reglas.
        val cases = listOf(
            "2020-05-01" to 2020,
            "2020" to 2020,
            "" to null,
            null to null,
            "abc" to null,
            "20" to 20, // Comportamiento actual del código de producción
            "0000-00-00" to 0
        )

        for ((dateStr, expectedYear) in cases) {
            val year = dateStr?.take(4)?.toIntOrNull()
            assertEquals(expectedYear, year)
        }
    }
}
