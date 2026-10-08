package com.example.bitacoradepeliculas.presentation.home

import com.example.bitacoradepeliculas.createTestMovieLog
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelExtendedTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var fakeMovieLogRepository: FakeMovieLogRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        fakeMovieLogRepository = FakeMovieLogRepository()
        viewModel = HomeViewModel(fakeMovieLogRepository, fakeAuthRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadMovieLogs_emptyList_setsEmptyState() = runTest(testDispatcher) {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value.movieLogsState
        assertTrue(state is MovieLogsState.Empty)
    }

    @Test
    fun loadMovieLogs_sessionExpired_emitsNavigateToLogin() = runTest(testDispatcher) {
        fakeMovieLogRepository.shouldFail = true
        fakeMovieLogRepository.errorToReturn = DataError.SessionExpired

        viewModel.loadMovieLogs()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value.movieLogsState
        assertTrue(state is MovieLogsState.Error)
        val event = viewModel.events.first()
        assertTrue(event is HomeEvent.NavigateToLogin)
    }

    @Test
    fun usernameFallback_whenNull_showsDefault() = runTest(testDispatcher) {
        fakeAuthRepository.userNameToReturn = null
        val vm = HomeViewModel(fakeMovieLogRepository, fakeAuthRepository)
        assertEquals("Usuario", vm.uiState.value.userName)
    }

    @Test
    fun refresh_updatesIsRefreshingState() = runTest(testDispatcher) {
        fakeMovieLogRepository.movieLogs.add(createTestMovieLog(1L, "Avatar"))
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRefreshing)
        val state = viewModel.uiState.value.movieLogsState
        assertTrue(state is MovieLogsState.Content)
    }

    @Test
    fun logout_preventsDoubleTap() = runTest(testDispatcher) {
        viewModel.logout()
        viewModel.logout() // double tap
        testDispatcher.scheduler.advanceUntilIdle()

        val event = viewModel.events.first()
        assertTrue(event is HomeEvent.NavigateToLogin)
    }

    @Test
    fun navigationEvents_emitCorrectPayload() = runTest(testDispatcher) {
        viewModel.onMovieLogClicked(42L)
        val detailEvent = viewModel.events.first()
        assertTrue(detailEvent is HomeEvent.NavigateToMovieLogDetail)
        assertEquals(42L, detailEvent.movieLogId)

        viewModel.onAddMovieLogClicked()
        val searchEvent = viewModel.events.first()
        assertTrue(searchEvent is HomeEvent.NavigateToSearchMovie)
    }
}
