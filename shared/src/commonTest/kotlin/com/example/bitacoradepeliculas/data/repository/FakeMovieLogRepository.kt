package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.data.model.MovieLog
import com.example.bitacoradepeliculas.data.model.NewMovieLog
import com.example.bitacoradepeliculas.domain.model.DataError

class FakeMovieLogRepository : MovieLogRepository {

    val movieLogs = mutableListOf<MovieLog>()
    var shouldFail = false
    var errorToReturn: DataError = DataError.Unknown("Error de prueba")
    var getMyMovieLogsCallCount = 0

    override suspend fun getMyMovieLogs(): Result<List<MovieLog>> {
        getMyMovieLogsCallCount++
        return if (shouldFail) {
            Result.failure(errorToReturn)
        } else {
            val sorted = movieLogs.sortedWith(
                compareByDescending<MovieLog> { it.logDate }
                    .thenByDescending { it.id }
            )
            Result.success(sorted)
        }
    }

    override suspend fun createMovieLog(newMovieLog: NewMovieLog): Result<Unit> {
        return if (shouldFail) {
            Result.failure(errorToReturn)
        } else {
            val nextId = (movieLogs.maxOfOrNull { it.id } ?: 0L) + 1L
            val log = MovieLog(
                id = nextId,
                movieTitle = newMovieLog.movieTitle,
                movieYear = newMovieLog.movieYear,
                moviePosterPath = newMovieLog.moviePosterPath,
                score = newMovieLog.score,
                logDate = newMovieLog.logDate,
                reviewText = newMovieLog.reviewText
            )
            movieLogs.add(log)
            Result.success(Unit)
        }
    }

    override suspend fun getMovieLogById(id: Long): Result<MovieLog?> {
        return Result.success(movieLogs.find { it.id == id })
    }

    override suspend fun updateMovieLog(movieLog: MovieLog): Result<Unit> {
        val index = movieLogs.indexOfFirst { it.id == movieLog.id }
        if (index != -1) {
            movieLogs[index] = movieLog
        }
        return Result.success(Unit)
    }

    override suspend fun deleteMovieLog(id: Long): Result<Unit> {
        movieLogs.removeAll { it.id == id }
        return Result.success(Unit)
    }
}
