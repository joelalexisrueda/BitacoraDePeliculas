package com.example.bitacoradepeliculas.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitacoradepeliculas.data.repository.MovieSearchRepository
import com.example.bitacoradepeliculas.domain.model.DirectorState
import com.example.bitacoradepeliculas.domain.model.MovieSearchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchMovieViewModel(
    private val movieSearchRepository: MovieSearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchMovieUiState())
    val uiState: StateFlow<SearchMovieUiState> = _uiState.asStateFlow()

    private val _events = Channel<SearchMovieEvent>(Channel.BUFFERED)
    val events: Flow<SearchMovieEvent> = _events.receiveAsFlow()

    private val _queryFlow = MutableStateFlow("")
    private var isNavigating = false

    init {
        viewModelScope.launch {
            _queryFlow
                .debounce(500.milliseconds)
                .map { it.trim() }
                .distinctUntilChanged()
                .collectLatest { query ->
                    executeSearch(query)
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        _queryFlow.value = newQuery
    }

    fun clearQuery() {
        onQueryChange("")
    }

    fun onSearchAction() {
        val currentQuery = _uiState.value.query.trim()
        executeSearch(currentQuery)
    }

    fun retry() {
        onSearchAction()
    }

    fun onMovieSelected(movie: MovieSearchResult) {
        if (isNavigating) return
        isNavigating = true
        viewModelScope.launch {
            _events.send(
                SearchMovieEvent.NavigateToLogMovie(
                    title = movie.title,
                    year = movie.year,
                    posterPath = movie.posterPath
                )
            )
            isNavigating = false
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _events.send(SearchMovieEvent.NavigateBack)
        }
    }

    private fun executeSearch(query: String) {
        if (query.length < 2) {
            _uiState.update { it.copy(searchState = SearchState.Idle) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(searchState = SearchState.Loading) }
            movieSearchRepository.searchMovies(query)
                .onSuccess { results ->
                    if (results.isEmpty()) {
                        _uiState.update { it.copy(searchState = SearchState.Empty) }
                    } else {
                        _uiState.update { it.copy(searchState = SearchState.Results(results)) }
                        loadDirectorsForResults(results)
                    }
                }
                .onFailure { error ->
                    val errorMsg = error.message ?: "Error al buscar películas."
                    _uiState.update { it.copy(searchState = SearchState.Error(errorMsg)) }
                }
        }
    }

    private suspend fun loadDirectorsForResults(results: List<MovieSearchResult>) {
        val semaphore = Semaphore(4)
        results.forEach { movie ->
            viewModelScope.launch {
                semaphore.withPermit {
                    movieSearchRepository.getDirector(movie.tmdbId)
                        .onSuccess { directorName ->
                            updateMovieDirectorState(movie.tmdbId, DirectorState.Loaded(directorName))
                        }
                        .onFailure {
                            updateMovieDirectorState(movie.tmdbId, DirectorState.Failed)
                        }
                }
            }
        }
    }

    private fun updateMovieDirectorState(tmdbId: Long, newDirectorState: DirectorState) {
        _uiState.update { currentState ->
            val searchState = currentState.searchState
            if (searchState is SearchState.Results) {
                val updatedItems = searchState.items.map { item ->
                    if (item.tmdbId == tmdbId) {
                        item.copy(directorState = newDirectorState)
                    } else {
                        item
                    }
                }
                currentState.copy(searchState = SearchState.Results(updatedItems))
            } else {
                currentState
            }
        }
    }
}
