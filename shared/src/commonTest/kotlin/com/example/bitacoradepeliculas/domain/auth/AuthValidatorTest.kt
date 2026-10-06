package com.example.bitacoradepeliculas.domain.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthValidatorTest {

    @Test
    fun validateEmail_withValidEmail_returnsNull() {
        val result = AuthValidator.validateEmail("usuario@example.com")
        assertNull(result)
    }

    @Test
    fun validateEmail_withInvalidEmail_returnsError() {
        val result = AuthValidator.validateEmail("correo-invalido")
        assertNotNull(result)
        assertEquals("Ingresa un correo electrónico válido.", result)
    }

    @Test
    fun validateEmail_withEmptyEmail_returnsError() {
        val result = AuthValidator.validateEmail("")
        assertNotNull(result)
        assertEquals("El correo electrónico es requerido.", result)
    }

    @Test
    fun validateName_withValidName_returnsNull() {
        val result = AuthValidator.validateName("Juan Pérez")
        assertNull(result)
    }

    @Test
    fun validateName_withShortName_returnsError() {
        val result = AuthValidator.validateName("A")
        assertNotNull(result)
        assertEquals("El nombre debe tener al menos 2 caracteres.", result)
    }

    @Test
    fun validatePassword_withShortPassword_returnsError() {
        val result = AuthValidator.validatePassword("12345")
        assertNotNull(result)
        assertEquals("La contraseña debe tener al menos 6 caracteres.", result)
    }

    @Test
    fun validateConfirmPassword_mismatch_returnsError() {
        val result = AuthValidator.validateConfirmPassword("password123", "diferente")
        assertNotNull(result)
        assertEquals("Las contraseñas no coinciden.", result)
    }
}
