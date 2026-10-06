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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

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
    fun login_withInvalidEmail_setsEmailError() = runTest(testDispatcher) {
        viewModel.onEmailChange("correo-invalido")
        viewModel.onPasswordChange("123456")

        viewModel.login()

        val state = viewModel.uiState.value
        assertNotNull(state.emailError)
        assertEquals(0, fakeRepository.loginCallCount)
    }

    @Test
    fun login_withValidInput_triggersSuccessEvent() = runTest(testDispatcher) {
        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("123456")

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeRepository.loginCallCount)
        val event = viewModel.events.first()
        assertTrue(event is AuthEvent.Success)
    }

    @Test
    fun login_whenRepositoryFails_setsGeneralError() = runTest(testDispatcher) {
        fakeRepository.shouldFailLogin = true
        fakeRepository.loginError = AuthError.InvalidCredentials

        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("wrongpassword")

        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Credenciales inválidas. Revisa tu correo o contraseña.", state.generalError)
        assertNull(state.emailError)
    }
}
