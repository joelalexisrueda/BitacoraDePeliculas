package com.example.bitacoradepeliculas.data.util

import kotlinx.coroutines.CancellationException

/**
 * Ejecuta un bloque de código capturando excepciones genéricas en un [Result],
 * relanzando explícitamente [CancellationException] para permitir la cancelación estructurada de Corrutinas.
 */
inline fun <T> safeCall(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
