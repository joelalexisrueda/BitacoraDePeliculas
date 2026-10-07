package com.example.bitacoradepeliculas.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitacoradepeliculas.data.repository.AuthRepository
import com.example.bitacoradepeliculas.data.repository.ReviewRepository
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
    private val reviewRepository: ReviewRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    init {
        val userName = authRepository.currentUserName() ?: "Usuario"
        _uiState.update { it.copy(userName = userName) }
        loadReviews()
    }

    fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(reviewsState = ReviewsState.Loading) }
            fetchReviews()
        }
    }

    fun retry() {
        loadReviews()
    }

    fun refresh() {
        if (_uiState.value.isRefreshing) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            fetchReviews()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun onResumed() {
        val currentState = _uiState.value.reviewsState
        if (currentState is ReviewsState.Content || currentState is ReviewsState.Empty) {
            viewModelScope.launch {
                reviewRepository.getMyReviews()
                    .onSuccess { reviews ->
                        val newState = if (reviews.isEmpty()) ReviewsState.Empty else ReviewsState.Content(reviews)
                        _uiState.update { it.copy(reviewsState = newState) }
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

    private suspend fun fetchReviews() {
        reviewRepository.getMyReviews()
            .onSuccess { reviews ->
                val newState = if (reviews.isEmpty()) ReviewsState.Empty else ReviewsState.Content(reviews)
                _uiState.update { it.copy(reviewsState = newState) }
            }
            .onFailure { error ->
                if (error is DataError.SessionExpired) {
                    _uiState.update { it.copy(reviewsState = ReviewsState.Error("Sesión expirada")) }
                    _events.send(HomeEvent.NavigateToLogin)
                } else {
                    val msg = error.message ?: "Error al cargar las reseñas."
                    _uiState.update { it.copy(reviewsState = ReviewsState.Error(msg)) }
                }
            }
    }

    fun onReviewClicked(reviewId: Long) {
        viewModelScope.launch {
            _events.send(HomeEvent.NavigateToReviewDetail(reviewId))
        }
    }

    fun onAddReviewClicked() {
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
