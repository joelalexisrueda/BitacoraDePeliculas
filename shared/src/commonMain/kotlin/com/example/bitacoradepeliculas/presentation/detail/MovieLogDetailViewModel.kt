package com.example.bitacoradepeliculas.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class MovieLogDetailViewModel(
    private val movieLogRepository: MovieLogRepository,
    val movieLogId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieLogDetailUiState())
    val uiState: StateFlow<MovieLogDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<MovieLogDetailEvent>(Channel.BUFFERED)
    val events: Flow<MovieLogDetailEvent> = _events.receiveAsFlow()

    private var isNavigatingToEdit = false

    init {
        loadMovieLog()
    }

    fun loadMovieLog() {
        viewModelScope.launch {
            _uiState.update { it.copy(detailState = MovieLogDetailState.Loading) }
            movieLogRepository.getById(movieLogId)
                .onSuccess { log ->
                    if (log != null) {
                        _uiState.update { it.copy(detailState = MovieLogDetailState.Content(log)) }
                    } else {
                        _uiState.update { it.copy(detailState = MovieLogDetailState.NotFound) }
                    }
                }
                .onFailure { error ->
                    if (error is DataError.SessionExpired) {
                        _events.send(MovieLogDetailEvent.NavigateToLogin)
                    } else {
                        val msg = error.message ?: "Error al cargar la reseña."
                        _uiState.update { it.copy(detailState = MovieLogDetailState.Error(msg)) }
                    }
                }
        }
    }

    fun onResumed() {
        viewModelScope.launch {
            movieLogRepository.getById(movieLogId)
                .onSuccess { log ->
                    if (log != null) {
                        _uiState.update { it.copy(detailState = MovieLogDetailState.Content(log)) }
                    } else {
                        _uiState.update { it.copy(detailState = MovieLogDetailState.NotFound) }
                    }
                }
                .onFailure { error ->
                    if (error is DataError.SessionExpired) {
                        _events.send(MovieLogDetailEvent.NavigateToLogin)
                    } else {
                        val msg = error.message ?: "Error al actualizar la reseña."
                        _events.send(MovieLogDetailEvent.ShowSnackbar(msg))
                    }
                }
        }
    }

    fun retry() {
        loadMovieLog()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _events.send(MovieLogDetailEvent.NavigateBack)
        }
    }

    fun onEditClicked() {
        if (isNavigatingToEdit) return
        isNavigatingToEdit = true
        viewModelScope.launch {
            _events.send(MovieLogDetailEvent.NavigateToEdit(movieLogId))
            isNavigatingToEdit = false
        }
    }

    fun onDeleteClicked() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }

    fun onDeleteDismissed() {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
    }

    fun confirmDelete() {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(isDeleting = true) }

        viewModelScope.launch {
            movieLogRepository.delete(movieLogId)
                .onSuccess {
                    _uiState.update { it.copy(isDeleteDialogVisible = false, isDeleting = false) }
                    _events.send(MovieLogDetailEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isDeleteDialogVisible = false, isDeleting = false) }
                    if (error is DataError.SessionExpired) {
                        _events.send(MovieLogDetailEvent.NavigateToLogin)
                    } else {
                        val msg = error.message ?: "Error al eliminar la reseña."
                        _events.send(MovieLogDetailEvent.ShowSnackbar(msg))
                    }
                }
        }
    }
}
