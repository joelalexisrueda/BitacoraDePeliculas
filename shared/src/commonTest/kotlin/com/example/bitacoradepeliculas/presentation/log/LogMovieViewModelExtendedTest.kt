package com.example.bitacoradepeliculas.presentation.log

import com.example.bitacoradepeliculas.data.repository.FakeMovieLogRepository
import com.example.bitacoradepeliculas.domain.util.FakeTodayProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LogMovieViewModelExtendedTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieLogRepository
    private lateinit var fakeTodayProvider: FakeTodayProvider

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMovieLogRepository()
        fakeTodayProvider = FakeTodayProvider(LocalDate(2026, 6, 15))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun reviewText_truncatesTo1000CharsMax() = runTest(testDispatcher) {
        val viewModel = LogMovieViewModel(
            movieLogRepository = fakeRepository,
            todayProvider = fakeTodayProvider,
            initialTitle = "Película & Acentos Ñoño / Test",
            initialYear = null,
            initialPosterPath = null
        )

        val text1001 = "A".repeat(1001)
        viewModel.onReviewTextChange(text1001)
        assertEquals(1000, viewModel.uiState.value.reviewText.length)

        // Blank text saved as null
        viewModel.onReviewTextChange("   ")
        viewModel.saveMovieLog()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(fakeRepository.movieLogs.first().reviewText)
        assertEquals("Película & Acentos Ñoño / Test", fakeRepository.movieLogs.first().movieTitle)
        assertNull(fakeRepository.movieLogs.first().movieYear)
        assertNull(fakeRepository.movieLogs.first().moviePosterPath)
    }
}
