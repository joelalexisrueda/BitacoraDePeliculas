package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.data.util.safeCall
import com.example.bitacoradepeliculas.domain.auth.toAuthError
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

interface AuthRepository {
    val sessionStatus: StateFlow<SessionStatus>
    fun currentUserName(): String?
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String, name: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    override val sessionStatus: StateFlow<SessionStatus>
        get() = supabaseClient.auth.sessionStatus

    override fun currentUserName(): String? {
        val user = supabaseClient.auth.currentUserOrNull() ?: return null
        return user.userMetadata?.get("name")?.jsonPrimitive?.contentOrNull
            ?: user.userMetadata?.get("name")?.toString()?.replace("\"", "")
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return safeCall {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it.toAuthError()) }
        )
    }

    override suspend fun register(email: String, password: String, name: String): Result<Unit> {
        return safeCall {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("name", name)
                }
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it.toAuthError()) }
        )
    }

    override suspend fun logout(): Result<Unit> {
        return safeCall {
            supabaseClient.auth.signOut()
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it.toAuthError()) }
        )
    }
}
