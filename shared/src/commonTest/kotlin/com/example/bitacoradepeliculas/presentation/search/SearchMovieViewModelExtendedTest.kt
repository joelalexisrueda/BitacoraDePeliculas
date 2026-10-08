package com.example.bitacoradepeliculas.presentation.search

import com.example.bitacoradepeliculas.createTestMovieSearchResult
import com.example.bitacoradepeliculas.data.repository.FakeMovieSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchMovieViewModelExtendedTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeMovieSearchRepository
    private lateinit var viewModel: SearchMovieViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeMovieSearchRepository()
        viewModel = SearchMovieViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun debounceExactTiming_doesNotSearchAt499ms_searchesAt500ms() = runTest(testDispatcher) {
        fakeRepository.searchResults.add(createTestMovieSearchResult(1L, "Matrix"))

        viewModel.onQueryChange("Matrix")
        testDispatcher.scheduler.advanceTimeBy(499)
        assertEquals(0, fakeRepository.searchCallCount)

        testDispatcher.scheduler.advanceTimeBy(1)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, fakeRepository.searchCallCount)
    }

    @Test
    fun blankQuery_resetsToIdle_withoutCallingRepository() = runTest(testDispatcher) {
        viewModel.onQueryChange("   ")
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, fakeRepository.searchCallCount)
        assertTrue(viewModel.uiState.value.searchState is SearchState.Idle)
    }

    @Test
    fun distinctUntilChanged_sameQueryDoesNotRepeatSearch() = runTest(testDispatcher) {
        fakeRepository.searchResults.add(createTestMovieSearchResult(1L, "Avatar"))

        viewModel.onQueryChange("Avatar")
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeRepository.searchCallCount)

        // Type same query again
        viewModel.onQueryChange("Avatar")
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        // Still 1 call because distinctUntilChanged filtered it out
        assertEquals(1, fakeRepository.searchCallCount)
    }
}
