package com.example.bitacoradepeliculas.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitacoradepeliculas.data.model.MovieLogUpdate
import com.example.bitacoradepeliculas.data.repository.MovieLogRepository
import com.example.bitacoradepeliculas.domain.model.DataError
import com.example.bitacoradepeliculas.domain.util.MovieLogFormValidator
import com.example.bitacoradepeliculas.domain.util.RatingUtils
import com.example.bitacoradepeliculas.domain.util.ReviewTextNormalizer
import com.example.bitacoradepeliculas.domain.util.TodayProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class EditMovieLogViewModel(
    private val movieLogRepository: MovieLogRepository,
    private val todayProvider: TodayProvider,
    val movieLogId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditMovieLogUiState())
    val uiState: StateFlow<EditMovieLogUiState> = _uiState.asStateFlow()

    private val _events = Channel<EditMovieLogEvent>(Channel.BUFFERED)
    val events: Flow<EditMovieLogEvent> = _events.receiveAsFlow()

    init {
        loadMovieLog()
    }

    fun loadMovieLog() {
        viewModelScope.launch {
            _uiState.update { it.copy(editState = EditMovieLogState.Loading) }
            movieLogRepository.getById(movieLogId)
                .onSuccess { log ->
                    if (log != null) {
                        _uiState.update {
                            it.copy(
                                editState = EditMovieLogState.Editing(
                                    movieTitle = log.movieTitle,
                                    movieYear = log.movieYear,
                                    moviePosterPath = log.moviePosterPath
                                ),
                                score = log.score,
                                logDate = log.logDate,
                                reviewText = log.reviewText ?: "",
                                originalScore = log.score,
                                originalLogDate = log.logDate,
                                originalReviewText = log.reviewText
                            )
                        }
                    } else {
                        _uiState.update { it.copy(editState = EditMovieLogState.NotFound) }
                    }
                }
                .onFailure { error ->
                    if (error is DataError.SessionExpired) {
                        _events.send(EditMovieLogEvent.NavigateToLogin)
                    } else {
                        val msg = error.message ?: "Error al cargar el registro para editar."
                        _uiState.update { it.copy(editState = EditMovieLogState.Error(msg)) }
                    }
                }
        }
    }

    fun retry() {
        loadMovieLog()
    }

    fun onScoreChanged(newScore: Double) {
        val clamped = newScore.coerceIn(0.0, 10.0)
        _uiState.update { it.copy(score = clamped) }
    }

    fun onScoreChanged(starIndex: Int, isLeftHalf: Boolean) {
        val currentScore = _uiState.value.score
        val newScore = RatingUtils.calculateScore(starIndex, isLeftHalf, currentScore)
        onScoreChanged(newScore)
    }

    fun onDateSelected(newDate: LocalDate) {
        val today = todayProvider.today()
        val validDate = MovieLogFormValidator.validateDate(newDate, today)
        _uiState.update { it.copy(logDate = validDate, isDatePickerVisible = false) }
    }

    fun onReviewTextChange(newText: String) {
        val truncated = MovieLogFormValidator.validateReviewText(newText)
        _uiState.update { it.copy(reviewText = truncated) }
    }

    fun onDatePickerVisibilityChanged(visible: Boolean) {
        _uiState.update { it.copy(isDatePickerVisible = visible) }
    }

    fun onBackAttempt() {
        if (_uiState.value.isSaving) return
        if (_uiState.value.isDirty) {
            _uiState.update { it.copy(isDiscardDialogVisible = true) }
        } else {
            viewModelScope.launch {
                _events.send(EditMovieLogEvent.NavigateBack)
            }
        }
    }

    fun onDiscardDialogDismissed() {
        _uiState.update { it.copy(isDiscardDialogVisible = false) }
    }

    fun confirmDiscard() {
        _uiState.update { it.copy(isDiscardDialogVisible = false) }
        viewModelScope.launch {
            _events.send(EditMovieLogEvent.NavigateBack)
        }
    }

    fun saveChanges() {
        val state = _uiState.value
        if (state.isSaving) return

        if (!state.isDirty) {
            viewModelScope.launch {
                _events.send(EditMovieLogEvent.NavigateBack)
            }
            return
        }

        val today = todayProvider.today()
        val validatedDate = MovieLogFormValidator.validateDate(state.logDate, today)
        val normalizedText = ReviewTextNormalizer.normalize(state.reviewText)

        val update = MovieLogUpdate(
            score = state.score,
            logDate = validatedDate,
            reviewText = normalizedText
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            movieLogRepository.update(movieLogId, update)
                .onSuccess { updatedLog ->
                    _uiState.update { it.copy(isSaving = false) }
                    if (updatedLog != null) {
                        _events.send(EditMovieLogEvent.NavigateBack)
                    } else {
                        _uiState.update { it.copy(editState = EditMovieLogState.NotFound) }
                        _events.send(EditMovieLogEvent.ShowSnackbar("Esta reseña ya no existe"))
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    if (error is DataError.SessionExpired) {
                        _events.send(EditMovieLogEvent.NavigateToLogin)
                    } else {
                        val msg = error.message ?: "Error al actualizar la reseña."
                        _events.send(EditMovieLogEvent.ShowSnackbar(msg))
                    }
                }
        }
    }
}
