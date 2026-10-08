package com.example.bitacoradepeliculas.presentation.auth

import com.example.bitacoradepeliculas.data.repository.FakeAuthRepository
import com.example.bitacoradepeliculas.domain.auth.AuthError
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelExtendedTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: LoginViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAuthRepository()
        viewModel = LoginViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isClean() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertNull(state.generalError)
        assertFalse(state.isLoading)
    }

    @Test
    fun login_emptyFields_showsValidationErrors() = runTest(testDispatcher) {
        viewModel.login()
        val state = viewModel.uiState.value
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
        assertEquals(0, fakeRepository.loginCallCount)
    }

    @Test
    fun typing_clearsValidationAndGeneralErrors() = runTest(testDispatcher) {
        viewModel.login() // triggers validation errors
        viewModel.onEmailChange("user@example.com")
        assertNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun login_preventsDoubleTap() = runTest(testDispatcher) {
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")

        viewModel.login()
        viewModel.login() // segundo toque síncrono
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeRepository.loginCallCount)
    }

    @Test
    fun login_failureAndRetrySuccess() = runTest(testDispatcher) {
        fakeRepository.shouldFailLogin = true
        fakeRepository.loginError = AuthError.InvalidCredentials

        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Credenciales inválidas. Revisa tu correo o contraseña.", viewModel.uiState.value.generalError)

        // Retry with success
        fakeRepository.shouldFailLogin = false
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        val event = viewModel.events.first()
        assertTrue(event is AuthEvent.Success)
    }
}
