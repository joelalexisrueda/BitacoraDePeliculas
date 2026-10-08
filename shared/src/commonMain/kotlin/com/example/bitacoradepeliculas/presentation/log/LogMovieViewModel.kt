package com.example.bitacoradepeliculas.presentation.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitacoradepeliculas.data.model.NewMovieLog
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

class LogMovieViewModel(
    private val movieLogRepository: MovieLogRepository,
    private val todayProvider: TodayProvider,
    initialTitle: String,
    initialYear: Int?,
    initialPosterPath: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LogMovieUiState(
            movieTitle = initialTitle,
            movieYear = initialYear,
            moviePosterPath = initialPosterPath,
            logDate = todayProvider.today()
        )
    )
    val uiState: StateFlow<LogMovieUiState> = _uiState.asStateFlow()

    private val _events = Channel<LogMovieEvent>(Channel.BUFFERED)
    val events: Flow<LogMovieEvent> = _events.receiveAsFlow()

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

    fun onBackClicked() {
        viewModelScope.launch {
            _events.send(LogMovieEvent.NavigateBack)
        }
    }

    fun saveMovieLog() {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true) }

        val state = _uiState.value
        val today = todayProvider.today()
        val validatedDate = MovieLogFormValidator.validateDate(state.logDate, today)
        val normalizedText = ReviewTextNormalizer.normalize(state.reviewText)

        val newMovieLog = NewMovieLog(
            movieTitle = state.movieTitle,
            movieYear = state.movieYear,
            moviePosterPath = state.moviePosterPath,
            score = state.score,
            logDate = validatedDate,
            reviewText = normalizedText
        )

        viewModelScope.launch {
            movieLogRepository.createMovieLog(newMovieLog)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(LogMovieEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    if (error is DataError.SessionExpired) {
                        _events.send(LogMovieEvent.NavigateToLogin)
                    } else {
                        val msg = error.message ?: "Error al guardar el registro."
                        _events.send(LogMovieEvent.ShowSnackbar(msg))
                    }
                }
        }
    }
}
