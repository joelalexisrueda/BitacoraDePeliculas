package com.example.bitacoradepeliculas.domain.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
        assertNull(AuthValidator.validateName("Juan"))
        assertNull(AuthValidator.validateName("Ana María"))
        assertNull(AuthValidator.validateName("Ñoño"))
        assertNull(AuthValidator.validateName("Sofía"))
        assertNull(AuthValidator.validateName("🎸 Rock"))
    }

    @Test
    fun validateName_with1Char_returnsError() {
        val result = AuthValidator.validateName("A")
        assertNotNull(result)
        assertEquals("El nombre debe tener al menos 2 caracteres.", result)
    }

    @Test
    fun validateName_with14Chars_returnsNull() {
        assertNull(AuthValidator.validateName("12345678901234"))
    }

    @Test
    fun validateName_with15Chars_returnsNull() {
        assertNull(AuthValidator.validateName("123456789012345"))
    }

    @Test
    fun validateName_with16Chars_returnsError() {
        val result = AuthValidator.validateName("1234567890123456")
        assertNotNull(result)
        assertEquals("El nombre no puede tener más de 15 caracteres.", result)
    }

    @Test
    fun validateName_withBlank_returnsError() {
        val result = AuthValidator.validateName("   ")
        assertNotNull(result)
        assertEquals("El nombre es requerido.", result)
    }

    @Test
    fun validateName_withEdgeSpaces_trimsAndValidates() {
        assertNull(AuthValidator.validateName("  Ana  ")) // trims to "Ana" (3 chars)
    }

    @Test
    fun truncateName_truncatesAndRespectsSurrogatePairs() {
        val longText = "A".repeat(30)
        val truncated = AuthValidator.truncateName(longText)
        assertEquals(15, truncated.length)

        // Emoji test (surrogate pair)
        val emojiText = "🎸123456789012345" // Emoji (2 chars) + 15 chars = 17 chars total
        val truncatedEmoji = AuthValidator.truncateName(emojiText, 5)
        assertTrue(truncatedEmoji.length <= 5)
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
