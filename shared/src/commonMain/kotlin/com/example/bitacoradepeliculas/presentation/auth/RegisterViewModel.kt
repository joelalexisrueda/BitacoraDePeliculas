package com.example.bitacoradepeliculas.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bitacoradepeliculas.data.repository.AuthRepository
import com.example.bitacoradepeliculas.domain.auth.AuthValidator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val nameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isLoading: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events: Flow<AuthEvent> = _events.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null, generalError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, generalError = null) }
    }

    fun register() {
        if (_uiState.value.isLoading) return

        val state = _uiState.value
        val email = state.email
        val name = state.name
        val password = state.password
        val confirmPassword = state.confirmPassword

        val emailErr = AuthValidator.validateEmail(email)
        val nameErr = AuthValidator.validateName(name)
        val passwordErr = AuthValidator.validatePassword(password)
        val confirmErr = AuthValidator.validateConfirmPassword(password, confirmPassword)

        if (emailErr != null || nameErr != null || passwordErr != null || confirmErr != null) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    nameError = nameErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            authRepository.register(email.trim(), password, name.trim())
                .onSuccess {
                    _uiState.update { currentState -> currentState.copy(isLoading = false) }
                    _events.send(AuthEvent.Success)
                }
                .onFailure { error ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            generalError = error.message ?: "Error al registrarse."
                        )
                    }
                }
        }
    }

    fun clearGeneralError() {
        _uiState.update { it.copy(generalError = null) }
    }
}
