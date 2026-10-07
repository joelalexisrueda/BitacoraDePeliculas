package com.example.bitacoradepeliculas.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitacoradepeliculas.data.repository.AuthRepository
import com.example.bitacoradepeliculas.data.repository.MovieLogRepository
import com.example.bitacoradepeliculas.domain.model.DataError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val movieLogRepository: MovieLogRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    init {
        val userName = authRepository.currentUserName() ?: "Usuario"
        _uiState.update { it.copy(userName = userName) }
        loadMovieLogs()
    }

    fun loadMovieLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(movieLogsState = MovieLogsState.Loading) }
            fetchMovieLogs()
        }
    }

    fun retry() {
        loadMovieLogs()
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            fetchMovieLogs()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun onResumed() {
        val currentState = _uiState.value.movieLogsState
        if (currentState is MovieLogsState.Content || currentState is MovieLogsState.Empty) {
            viewModelScope.launch {
                movieLogRepository.getMyMovieLogs()
                    .onSuccess { movieLogs ->
                        val newState = if (movieLogs.isEmpty()) MovieLogsState.Empty else MovieLogsState.Content(movieLogs)
                        _uiState.update { it.copy(movieLogsState = newState) }
                    }
                    .onFailure { error ->
                        if (error is DataError.SessionExpired) {
                            _events.send(HomeEvent.NavigateToLogin)
                        } else {
                            val msg = error.message ?: "No se pudieron actualizar las reseñas."
                            _events.send(HomeEvent.ShowSnackbar(msg))
                        }
                    }
            }
        }
    }

    private suspend fun fetchMovieLogs() {
        movieLogRepository.getMyMovieLogs()
            .onSuccess { movieLogs ->
                val newState = if (movieLogs.isEmpty()) MovieLogsState.Empty else MovieLogsState.Content(movieLogs)
                _uiState.update { it.copy(movieLogsState = newState) }
            }
            .onFailure { error ->
                if (error is DataError.SessionExpired) {
                    _uiState.update { it.copy(movieLogsState = MovieLogsState.Error("Sesión expirada")) }
                    _events.send(HomeEvent.NavigateToLogin)
                } else {
                    val msg = error.message ?: "Error al cargar las reseñas."
                    _uiState.update { it.copy(movieLogsState = MovieLogsState.Error(msg)) }
                }
            }
    }

    fun onMovieLogClicked(movieLogId: Long) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToMovieLogDetail(movieLogId))
        }
    }

    fun onAddMovieLogClicked() {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToSearchMovie)
        }
    }

    /**
     * Cierra la sesión activa.
     * Nota de Navegación: Emitimos [HomeEvent.NavigateToLogin] para realizar la transición directa.
     * La navegación raíz en App.kt también observa [AuthRepository.sessionStatus], garantizando
     * que en caso de desautenticación forzada o limpieza de storage, se redirija a Login limpiando el backstack.
     */
    fun logout() {
        if (_uiState.value.isLoggingOut) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            authRepository.logout()
            _uiState.update { it.copy(isLoggingOut = false) }
            _events.send(HomeEvent.NavigateToLogin)
        }
    }
}
