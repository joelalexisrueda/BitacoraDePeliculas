package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.domain.auth.AuthError
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository : AuthRepository {

    private val _sessionStatus = MutableStateFlow<SessionStatus>(SessionStatus.NotAuthenticated())
    override val sessionStatus: StateFlow<SessionStatus> = _sessionStatus.asStateFlow()

    var userNameToReturn: String? = "Usuario"
    var shouldFailLogin = false
    var shouldFailRegister = false
    var loginError: AuthError = AuthError.InvalidCredentials
    var registerError: AuthError = AuthError.EmailAlreadyRegistered

    var loginCallCount = 0
    var registerCallCount = 0

    override fun currentUserName(): String? = userNameToReturn

    override suspend fun login(email: String, password: String): Result<Unit> {
        loginCallCount++
        return if (shouldFailLogin) {
            Result.failure(loginError)
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun register(email: String, password: String, name: String): Result<Unit> {
        registerCallCount++
        return if (shouldFailRegister) {
            Result.failure(registerError)
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun logout(): Result<Unit> {
        _sessionStatus.value = SessionStatus.NotAuthenticated()
        return Result.success(Unit)
    }
}
