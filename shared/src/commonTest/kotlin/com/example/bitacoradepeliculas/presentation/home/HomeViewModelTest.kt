package com.example.bitacoradepeliculas.presentation.home

import com.example.bitacoradepeliculas.data.model.MovieLog
import com.example.bitacoradepeliculas.data.repository.FakeAuthRepository
import com.example.bitacoradepeliculas.data.repository.FakeMovieLogRepository
import com.example.bitacoradepeliculas.domain.model.DataError
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeMovieLogRepository: FakeMovieLogRepository
    private lateinit var fakeAuthRepository: FakeAuthRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeMovieLogRepository = FakeMovieLogRepository()
        fakeAuthRepository = FakeAuthRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_withEmptyMovieLogs_setsEmptyState() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeMovieLogRepository, fakeAuthRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Usuario", state.userName)
        assertTrue(state.movieLogsState is MovieLogsState.Empty)
    }

    @Test
    fun init_withMovieLogs_setsContentStateAndOrdersCorrectly() = runTest(testDispatcher) {
        val r1 = MovieLog(id = 1L, movieTitle = "Película A", score = 8.0, logDate = LocalDate(2026, 1, 10))
        val r2 = MovieLog(id = 2L, movieTitle = "Película B", score = 9.0, logDate = LocalDate(2026, 1, 10))
        val r3 = MovieLog(id = 3L, movieTitle = "Película C", score = 7.0, logDate = LocalDate(2025, 12, 31))

        fakeMovieLogRepository.movieLogs.addAll(listOf(r1, r3, r2))

        val viewModel = HomeViewModel(fakeMovieLogRepository, fakeAuthRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.movieLogsState is MovieLogsState.Content)
        val list = state.movieLogsState.movieLogs
        assertEquals(3, list.size)
        assertEquals(2L, list[0].id)
        assertEquals(1L, list[1].id)
        assertEquals(3L, list[2].id)
    }

    @Test
    fun retry_afterError_reloadsSuccessfully() = runTest(testDispatcher) {
        fakeMovieLogRepository.shouldFail = true
        fakeMovieLogRepository.errorToReturn = DataError.NoInternet

        val viewModel = HomeViewModel(fakeMovieLogRepository, fakeAuthRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.movieLogsState is MovieLogsState.Error)

        fakeMovieLogRepository.shouldFail = false
        fakeMovieLogRepository.movieLogs.add(MovieLog(id = 1L, movieTitle = "El Padrino", score = 10.0, logDate = LocalDate(2026, 5, 1)))
        
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.movieLogsState is MovieLogsState.Content)
    }

    @Test
    fun silentRefresh_whenFails_retainsCurrentContentAndEmitsSnackbar() = runTest(testDispatcher) {
        val initialMovieLog = MovieLog(id = 1L, movieTitle = "Matrix", score = 9.0, logDate = LocalDate(2026, 2, 1))
        fakeMovieLogRepository.movieLogs.add(initialMovieLog)

        val viewModel = HomeViewModel(fakeMovieLogRepository, fakeAuthRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.movieLogsState is MovieLogsState.Content)

        fakeMovieLogRepository.shouldFail = true
        fakeMovieLogRepository.errorToReturn = DataError.NoInternet

        viewModel.onResumed()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.movieLogsState is MovieLogsState.Content)
        val event = viewModel.events.first()
        assertTrue(event is HomeEvent.ShowSnackbar)
    }

    @Test
    fun logout_triggersNavigateToLoginAndPreventsDoubleCall() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeMovieLogRepository, fakeAuthRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.logout()
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        val event = viewModel.events.first()
        assertTrue(event is HomeEvent.NavigateToLogin)
        assertFalse(viewModel.uiState.value.isLoggingOut)
    }

    @Test
    fun sessionExpired_emitsNavigateToLogin() = runTest(testDispatcher) {
        fakeMovieLogRepository.shouldFail = true
        fakeMovieLogRepository.errorToReturn = DataError.SessionExpired

        val viewModel = HomeViewModel(fakeMovieLogRepository, fakeAuthRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = viewModel.events.first()
        assertTrue(event is HomeEvent.NavigateToLogin)
    }
}
