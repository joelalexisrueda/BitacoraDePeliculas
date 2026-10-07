package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.data.model.MovieLog
import com.example.bitacoradepeliculas.data.model.SupabaseTables
import com.example.bitacoradepeliculas.data.util.safeCall
import com.example.bitacoradepeliculas.domain.model.toDataError
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

interface MovieLogRepository {
    suspend fun getMyMovieLogs(): Result<List<MovieLog>>
    suspend fun createMovieLog(movieLog: MovieLog): Result<Unit>
    suspend fun getMovieLogById(id: Long): Result<MovieLog?>
    suspend fun updateMovieLog(movieLog: MovieLog): Result<Unit>
    suspend fun deleteMovieLog(id: Long): Result<Unit>
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

    override suspend fun createMovieLog(movieLog: MovieLog): Result<Unit> {
        return Result.failure(NotImplementedError("Será implementado en la siguiente fase"))
    }

    override suspend fun getMovieLogById(id: Long): Result<MovieLog?> {
        return Result.failure(NotImplementedError("Será implementado en la siguiente fase"))
    }

    override suspend fun updateMovieLog(movieLog: MovieLog): Result<Unit> {
        return Result.failure(NotImplementedError("Será implementado en la siguiente fase"))
    }

    override suspend fun deleteMovieLog(id: Long): Result<Unit> {
        return Result.failure(NotImplementedError("Será implementado en la siguiente fase"))
    }
}
