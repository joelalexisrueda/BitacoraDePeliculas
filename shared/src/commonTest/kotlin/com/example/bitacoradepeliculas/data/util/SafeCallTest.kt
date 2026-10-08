package com.example.bitacoradepeliculas.data.util

import com.example.bitacoradepeliculas.domain.auth.toAuthError
import com.example.bitacoradepeliculas.domain.model.DataError
import com.example.bitacoradepeliculas.domain.model.toDataError
import com.example.bitacoradepeliculas.domain.model.toTmdbError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SafeCallTest {

    @Test
    fun safeCall_rethrowsCancellationException() = runTest {
        assertFailsWith<CancellationException> {
            safeCall {
                throw CancellationException("Cancelled")
            }
        }
    }

    @Test
    fun safeCall_catchesOtherExceptions_returnsFailure() = runTest {
        val result = safeCall {
            throw IllegalStateException("Something failed")
        }
        assertTrue(result.isFailure)
    }

    @Test
    fun errorMapping_mapsExceptionsToTypedErrorsInSpanish() {
        val runtimeEx = RuntimeException("raw message")

        // toDataError
        val dataError = runtimeEx.toDataError()
        assertTrue(dataError is DataError.Unknown)
        assertEquals("raw message", dataError.message)

        // toAuthError
        val authError = runtimeEx.toAuthError()
        assertEquals("raw message", authError.message)

        // toTmdbError
        val tmdbError = runtimeEx.toTmdbError()
        assertEquals("raw message", tmdbError.message)
    }
}
