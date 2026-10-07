package com.example.bitacoradepeliculas.presentation.detail

import com.example.bitacoradepeliculas.data.model.MovieLog
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieLogDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieLogRepository
    private lateinit var viewModel: MovieLogDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMovieLogRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadMovieLog_success_setsContentState() = runTest(testDispatcher) {
        val sampleLog = MovieLog(
            id = 1L,
            movieTitle = "Interstellar",
            movieYear = 2014,
            moviePosterPath = "/interstellar.jpg",
            score = 9.5,
            logDate = LocalDate(2026, 6, 15),
            reviewText = "Impresionante."
        )
        fakeRepository.movieLogs.add(sampleLog)

        viewModel = MovieLogDetailViewModel(fakeRepository, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value.detailState
        assertTrue(state is MovieLogDetailState.Content)
        assertEquals("Interstellar", state.movieLog.movieTitle)
        assertEquals(9.5, state.movieLog.score)
    }

    @Test
    fun loadMovieLog_notFound_setsNotFoundState() = runTest(testDispatcher) {
        viewModel = MovieLogDetailViewModel(fakeRepository, 999L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value.detailState
        assertTrue(state is MovieLogDetailState.NotFound)
    }

    @Test
    fun confirmDelete_success_emitsNavigateToHome() = runTest(testDispatcher) {
        val sampleLog = MovieLog(
            id = 2L,
            movieTitle = "Matrix",
            movieYear = 1999,
            moviePosterPath = "/matrix.jpg",
            score = 10.0,
            logDate = LocalDate(2026, 6, 15),
            reviewText = "Cine puro."
        )
        fakeRepository.movieLogs.add(sampleLog)

        viewModel = MovieLogDetailViewModel(fakeRepository, 2L)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onDeleteClicked()
        assertTrue(viewModel.uiState.value.isDeleteDialogVisible)

        viewModel.confirmDelete()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeRepository.movieLogs.isEmpty())
        val event = viewModel.events.first()
        assertTrue(event is MovieLogDetailEvent.NavigateToHome)
    }

    @Test
    fun confirmDelete_sessionExpired_emitsNavigateToLogin() = runTest(testDispatcher) {
        val sampleLog = MovieLog(
            id = 3L,
            movieTitle = "Avatar",
            movieYear = 2009,
            moviePosterPath = "/avatar.jpg",
            score = 8.0,
            logDate = LocalDate(2026, 6, 15),
            reviewText = null
        )
        fakeRepository.movieLogs.add(sampleLog)

        viewModel = MovieLogDetailViewModel(fakeRepository, 3L)
        testDispatcher.scheduler.advanceUntilIdle()

        fakeRepository.shouldFail = true
        fakeRepository.errorToReturn = DataError.SessionExpired

        viewModel.confirmDelete()
        testDispatcher.scheduler.advanceUntilIdle()

        val event = viewModel.events.first()
        assertTrue(event is MovieLogDetailEvent.NavigateToLogin)
    }

    @Test
    fun onEditClicked_emitsNavigateToEdit() = runTest(testDispatcher) {
        val sampleLog = MovieLog(
            id = 4L,
            movieTitle = "Gladiator",
            movieYear = 2000,
            moviePosterPath = "/gladiator.jpg",
            score = 9.0,
            logDate = LocalDate(2026, 6, 15),
            reviewText = "¡Entretenido!"
        )
        fakeRepository.movieLogs.add(sampleLog)

        viewModel = MovieLogDetailViewModel(fakeRepository, 4L)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEditClicked()
        val event = viewModel.events.first()
        assertTrue(event is MovieLogDetailEvent.NavigateToEdit)
        assertEquals(4L, event.movieLogId)
    }
}
