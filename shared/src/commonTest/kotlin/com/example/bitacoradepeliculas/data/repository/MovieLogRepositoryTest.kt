package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.createTestMovieLog
import com.example.bitacoradepeliculas.data.model.MovieLogUpdate
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MovieLogRepositoryTest {

    private val repository = FakeMovieLogRepository()

    @Test
    fun getMyMovieLogs_sortsByLogDateDescendingThenByIdDescending() = runTest {
        val log1 = createTestMovieLog(id = 1L, movieTitle = "Older", logDate = LocalDate(2026, 1, 1))
        val log2 = createTestMovieLog(id = 2L, movieTitle = "Newer ID 2", logDate = LocalDate(2026, 6, 15))
        val log3 = createTestMovieLog(id = 3L, movieTitle = "Newer ID 3", logDate = LocalDate(2026, 6, 15))

        repository.movieLogs.addAll(listOf(log1, log2, log3))

        val result = repository.getMyMovieLogs().getOrThrow()
        assertEquals(3, result.size)
        assertEquals(3L, result[0].id) // Same date, higher ID first
        assertEquals(2L, result[1].id)
        assertEquals(1L, result[2].id) // Older date
    }

    @Test
    fun getById_nonExistent_returnsNull() = runTest {
        val result = repository.getById(999L).getOrThrow()
        assertNull(result)
    }

    @Test
    fun delete_isIdempotent() = runTest {
        val log = createTestMovieLog(id = 5L)
        repository.movieLogs.add(log)

        val delete1 = repository.delete(5L)
        assertTrue(delete1.isSuccess)
        assertTrue(repository.movieLogs.isEmpty())

        // Delete non-existent ID 5L again -> idempotent success
        val delete2 = repository.delete(5L)
        assertTrue(delete2.isSuccess)
    }

    @Test
    fun update_nonExistent_returnsNull() = runTest {
        val update = MovieLogUpdate(
            score = 10.0,
            logDate = LocalDate(2026, 6, 15),
            reviewText = "Excelente"
        )
        val result = repository.update(999L, update).getOrThrow()
        assertNull(result)
    }
}
