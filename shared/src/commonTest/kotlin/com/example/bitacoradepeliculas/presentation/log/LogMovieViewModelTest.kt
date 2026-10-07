package com.example.bitacoradepeliculas.presentation.log

import com.example.bitacoradepeliculas.data.repository.FakeMovieLogRepository
import com.example.bitacoradepeliculas.domain.model.DataError
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
class LogMovieViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieLogRepository
    private lateinit var fakeTodayProvider: FakeTodayProvider
    private lateinit var viewModel: LogMovieViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMovieLogRepository()
        fakeTodayProvider = FakeTodayProvider(LocalDate(2026, 6, 15))
        viewModel = LogMovieViewModel(
            movieLogRepository = fakeRepository,
            todayProvider = fakeTodayProvider,
            initialTitle = "Inception",
            initialYear = 2010,
            initialPosterPath = "/inception.jpg"
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasCorrectDefaults() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertEquals("Inception", state.movieTitle)
        assertEquals(2010, state.movieYear)
        assertEquals("/inception.jpg", state.moviePosterPath)
        assertEquals(10.0, state.score)
        assertEquals(LocalDate(2026, 6, 15), state.logDate)
        assertEquals("", state.reviewText)
        assertFalse(state.isSaving)
    }

    @Test
    fun onScoreChanged_updatesScore() = runTest(testDispatcher) {
        viewModel.onScoreChanged(8, true) // Star 8 left half -> 7.5
        assertEquals(7.5, viewModel.uiState.value.score)
    }

    @Test
    fun onDateSelected_rejectsFutureDate() = runTest(testDispatcher) {
        viewModel.onDateSelected(LocalDate(2026, 6, 20)) // Future date
        assertEquals(LocalDate(2026, 6, 15), viewModel.uiState.value.logDate) // capped to today
    }

    @Test
    fun saveMovieLog_success_emitsNavigateToHome() = runTest(testDispatcher) {
        viewModel.onReviewTextChange("  Muy buena película.  ")
        viewModel.saveMovieLog()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeRepository.movieLogs.size)
        val created = fakeRepository.movieLogs.first()
        assertEquals("Inception", created.movieTitle)
        assertEquals("Muy buena película.", created.reviewText)

        val event = viewModel.events.first()
        assertTrue(event is LogMovieEvent.NavigateToHome)
    }

    @Test
    fun saveMovieLog_sessionExpired_emitsNavigateToLogin() = runTest(testDispatcher) {
        fakeRepository.shouldFail = true
        fakeRepository.errorToReturn = DataError.SessionExpired

        viewModel.saveMovieLog()
        testDispatcher.scheduler.advanceUntilIdle()

        val event = viewModel.events.first()
        assertTrue(event is LogMovieEvent.NavigateToLogin)
    }

    @Test
    fun saveMovieLog_preventsDoubleTap() = runTest(testDispatcher) {
        viewModel.saveMovieLog()
        viewModel.saveMovieLog() // Second tap while saving
        testDispatcher.scheduler.advanceUntilIdle()

        // Only 1 creation call
        assertEquals(1, fakeRepository.movieLogs.size)
    }
}
