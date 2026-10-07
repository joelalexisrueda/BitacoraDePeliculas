package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.data.model.MovieLog
import com.example.bitacoradepeliculas.data.model.NewMovieLog
import com.example.bitacoradepeliculas.data.model.SupabaseTables
import com.example.bitacoradepeliculas.data.util.safeCall
import com.example.bitacoradepeliculas.domain.model.toDataError
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

interface MovieLogRepository {
    suspend fun getMyMovieLogs(): Result<List<MovieLog>>
    suspend fun getById(id: Long): Result<MovieLog?>
    suspend fun createMovieLog(newMovieLog: NewMovieLog): Result<Unit>
    suspend fun updateMovieLog(movieLog: MovieLog): Result<Unit>
    suspend fun delete(id: Long): Result<Unit>
}

class MovieLogRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : MovieLogRepository {

    override suspend fun getMyMovieLogs(): Result<List<MovieLog>> {
        return safeCall {
            supabaseClient.from(SupabaseTables.MOVIE_LOG)
                .select {
                    order(column = "log_date", order = Order.DESCENDING)
                    order(column = "id", order = Order.DESCENDING)
                }
                .decodeList<MovieLog>()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it.toDataError()) }
        )
    }

    override suspend fun getById(id: Long): Result<MovieLog?> {
        return safeCall {
            supabaseClient.from(SupabaseTables.MOVIE_LOG)
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<MovieLog>()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it.toDataError()) }
        )
    }

    override suspend fun createMovieLog(newMovieLog: NewMovieLog): Result<Unit> {
        return safeCall {
            supabaseClient.from(SupabaseTables.MOVIE_LOG)
                .insert(newMovieLog)
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it.toDataError()) }
        )
    }

    override suspend fun updateMovieLog(movieLog: MovieLog): Result<Unit> {
        return Result.failure(NotImplementedError("Será implementado en la siguiente fase"))
    }

    override suspend fun delete(id: Long): Result<Unit> {
        // El borrado en Supabase RLS es idempotente (si no existe o no pertenece al usuario, no borra filas y termina ok)
        return safeCall {
            supabaseClient.from(SupabaseTables.MOVIE_LOG)
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it.toDataError()) }
        )
    }
}
