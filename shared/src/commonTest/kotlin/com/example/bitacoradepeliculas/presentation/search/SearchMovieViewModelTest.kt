package com.example.bitacoradepeliculas.presentation.search

import com.example.bitacoradepeliculas.data.repository.FakeMovieSearchRepository
import com.example.bitacoradepeliculas.domain.model.DirectorState
import com.example.bitacoradepeliculas.domain.model.MovieSearchResult
import com.example.bitacoradepeliculas.domain.model.TmdbError
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
class SearchMovieViewModelTest {

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
    fun queryLessThan2Chars_returnsIdleStateWithoutCallingRepository() = runTest(testDispatcher) {
        viewModel.onQueryChange("A")
        testDispatcher.scheduler.advanceTimeBy(600) // Advance past debounce

        assertEquals(0, fakeRepository.searchCallCount)
        assertTrue(viewModel.uiState.value.searchState is SearchState.Idle)
    }

    @Test
    fun debounce_delaysSearchCallUntilUserStopsTyping() = runTest(testDispatcher) {
        fakeRepository.searchResults.add(
            MovieSearchResult(tmdbId = 1L, title = "Batman", year = 2022, posterPath = "/batman.jpg")
        )

        viewModel.onQueryChange("B")
        testDispatcher.scheduler.advanceTimeBy(200)
        viewModel.onQueryChange("Ba")
        testDispatcher.scheduler.advanceTimeBy(200)
        viewModel.onQueryChange("Bat")
        
        // Before 500ms debounce
        assertEquals(0, fakeRepository.searchCallCount)

        // Advance 500ms
        testDispatcher.scheduler.advanceTimeBy(500)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeRepository.searchCallCount)
        assertTrue(viewModel.uiState.value.searchState is SearchState.Results)
    }

    @Test
    fun searchMovies_emptyResults_setsEmptyState() = runTest(testDispatcher) {
        viewModel.onQueryChange("Inexistente")
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeRepository.searchCallCount)
        assertTrue(viewModel.uiState.value.searchState is SearchState.Empty)
    }

    @Test
    fun searchMovies_whenRepositoryFails_setsErrorStateAndCanRetry() = runTest(testDispatcher) {
        fakeRepository.shouldFailSearch = true
        fakeRepository.searchError = TmdbError.NoInternet

        viewModel.onQueryChange("Matrix")
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        val errorState = viewModel.uiState.value.searchState
        assertTrue(errorState is SearchState.Error)
        assertEquals("Sin conexión a internet. Revisa tu red.", errorState.message)

        // Retry successfully
        fakeRepository.shouldFailSearch = false
        fakeRepository.searchResults.add(
            MovieSearchResult(tmdbId = 10L, title = "Matrix", year = 1999, posterPath = "/matrix.jpg")
        )

        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.searchState is SearchState.Results)
    }

    @Test
    fun directorLoading_completesAsynchronouslyPerMovie() = runTest(testDispatcher) {
        fakeRepository.searchResults.add(
            MovieSearchResult(tmdbId = 100L, title = "Inception", year = 2010, posterPath = "/inception.jpg")
        )
        fakeRepository.directorsMap[100L] = "Christopher Nolan"

        viewModel.onQueryChange("Inception")
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.advanceUntilIdle()

        val resultsState = viewModel.uiState.value.searchState
        assertTrue(resultsState is SearchState.Results)

        val movie = resultsState.items.first()
        assertTrue(movie.directorState is DirectorState.Loaded)
        assertEquals("Christopher Nolan", (movie.directorState as DirectorState.Loaded).name)
    }

    @Test
    fun onMovieSelected_emitsNavigateToLogMovieEvent() = runTest(testDispatcher) {
        val movie = MovieSearchResult(
            tmdbId = 500L,
            title = "Interstellar",
            year = 2014,
            posterPath = "/interstellar.jpg"
        )

        viewModel.onMovieSelected(movie)

        val event = viewModel.events.first()
        assertTrue(event is SearchMovieEvent.NavigateToLogMovie)
        assertEquals("Interstellar", event.title)
        assertEquals(2014, event.year)
        assertEquals("/interstellar.jpg", event.posterPath)
    }
}
