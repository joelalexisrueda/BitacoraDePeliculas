package com.example.bitacoradepeliculas.presentation.auth

import com.example.bitacoradepeliculas.data.repository.FakeAuthRepository
import com.example.bitacoradepeliculas.domain.auth.AuthError
import com.example.bitacoradepeliculas.domain.auth.AuthValidator
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: RegisterViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAuthRepository()
        viewModel = RegisterViewModel(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onNameChange_truncatesTo15CharsMax() = runTest(testDispatcher) {
        viewModel.onNameChange("12345678901234567890") // 20 chars
        assertEquals(AuthValidator.NAME_MAX_LENGTH, viewModel.uiState.value.name.length)
        assertEquals("123456789012345", viewModel.uiState.value.name)
    }

    @Test
    fun onNameChange_doesNotSplitSurrogatePair() = runTest(testDispatcher) {
        val textWithEmoji = "🎸123456789012345"
        viewModel.onNameChange(textWithEmoji)
        val nameResult = viewModel.uiState.value.name
        assertTrue(nameResult.length <= AuthValidator.NAME_MAX_LENGTH)
    }

    @Test
    fun register_withPasswordMismatch_setsConfirmPasswordError() = runTest(testDispatcher) {
        viewModel.onEmailChange("nuevo@example.com")
        viewModel.onNameChange("Usuario Test")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("diferente")

        viewModel.register()

        val state = viewModel.uiState.value
        assertNotNull(state.confirmPasswordError)
        assertEquals(0, fakeRepository.registerCallCount)
    }

    @Test
    fun register_withValidInput_triggersSuccessEvent() = runTest(testDispatcher) {
        viewModel.onEmailChange("nuevo@example.com")
        viewModel.onNameChange("  Usuario Test  ")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakeRepository.registerCallCount)
        assertEquals("Usuario Test", fakeRepository.lastRegisteredName)
        val event = viewModel.events.first()
        assertTrue(event is AuthEvent.Success)
    }

    @Test
    fun register_whenEmailAlreadyExists_setsGeneralError() = runTest(testDispatcher) {
        fakeRepository.shouldFailRegister = true
        fakeRepository.registerError = AuthError.EmailAlreadyRegistered

        viewModel.onEmailChange("existente@example.com")
        viewModel.onNameChange("Usuario Exist")
        viewModel.onPasswordChange("password123")
        viewModel.onConfirmPasswordChange("password123")

        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("El correo electrónico ya está registrado.", state.generalError)
    }
}
