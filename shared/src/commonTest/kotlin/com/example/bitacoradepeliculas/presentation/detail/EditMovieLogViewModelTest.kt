package com.example.bitacoradepeliculas.presentation.detail

import com.example.bitacoradepeliculas.data.model.MovieLog
import com.example.bitacoradepeliculas.data.repository.FakeMovieLogRepository
import com.example.bitacoradepeliculas.domain.util.FakeTodayProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EditMovieLogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieLogRepository
    private lateinit var fakeTodayProvider: FakeTodayProvider
    private lateinit var viewModel: EditMovieLogViewModel

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
    fun loadMovieLog_preloadsFormCorrectly() = runTest(testDispatcher) {
        val sampleLog = MovieLog(
            id = 1L,
            movieTitle = "Inception",
            movieYear = 2010,
            moviePosterPath = "/inception.jpg",
            score = 9.0,
            logDate = LocalDate(2026, 6, 10),
            reviewText = "Excelente película."
        )
        fakeRepository.movieLogs.add(sampleLog)

        viewModel = EditMovieLogViewModel(fakeRepository, fakeTodayProvider, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(9.0, state.score)
        assertEquals(LocalDate(2026, 6, 10), state.logDate)
        assertEquals("Excelente película.", state.reviewText)
        assertFalse(state.isDirty)
    }

    @Test
    fun isDirty_detectsChangesAccurately() = runTest(testDispatcher) {
        val sampleLog = MovieLog(
            id = 1L,
            movieTitle = "Inception",
            movieYear = 2010,
            moviePosterPath = "/inception.jpg",
            score = 9.0,
            logDate = LocalDate(2026, 6, 10),
            reviewText = "Excelente película."
        )
        fakeRepository.movieLogs.add(sampleLog)

        viewModel = EditMovieLogViewModel(fakeRepository, fakeTodayProvider, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isDirty)

        // Change score
        viewModel.onScoreChanged(8.0)
        assertTrue(viewModel.uiState.value.isDirty)

        // Revert score
        viewModel.onScoreChanged(9.0)
        assertFalse(viewModel.uiState.value.isDirty)
    }

    @Test
    fun saveChanges_withoutChanges_emitsNavigateBackDirectly() = runTest(testDispatcher) {
        val sampleLog = MovieLog(
            id = 1L,
            movieTitle = "Inception",
            movieYear = 2010,
            moviePosterPath = "/inception.jpg",
            score = 9.0,
            logDate = LocalDate(2026, 6, 10),
            reviewText = "Excelente."
        )
        fakeRepository.movieLogs.add(sampleLog)

        viewModel = EditMovieLogViewModel(fakeRepository, fakeTodayProvider, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.saveChanges()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, fakeRepository.updateCallCount)
        val event = viewModel.events.first()
        assertTrue(event is EditMovieLogEvent.NavigateBack)
    }

    @Test
    fun saveChanges_withChanges_updatesAndEmitsNavigateBack() = runTest(testDispatcher) {
        val sampleLog = MovieLog(
            id = 1L,
            movieTitle = "Inception",
            movieYear = 2010,
            moviePosterPath = "/inception.jpg",
            score = 9.0,
            logDate = LocalDate(2026, 6, 10),
            reviewText = "Excelente."
        )
        fakeRepository.movieLogs.add(sampleLog)

        viewModel = EditMovieLogViewModel(fakeRepository, fakeTodayProvider, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onScoreChanged(10.0)
        viewModel.saveChanges()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeRepository.updateCallCount)
        assertEquals(10.0, fakeRepository.movieLogs.first().score)
        val event = viewModel.events.first()
        assertTrue(event is EditMovieLogEvent.NavigateBack)
    }
}
