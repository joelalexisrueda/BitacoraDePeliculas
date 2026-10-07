package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.config.AppConfig
import com.example.bitacoradepeliculas.data.remote.tmdb.model.TmdbCreditsResponse
import com.example.bitacoradepeliculas.data.remote.tmdb.model.TmdbSearchResponse
import com.example.bitacoradepeliculas.data.util.safeCall
import com.example.bitacoradepeliculas.domain.model.DirectorState
import com.example.bitacoradepeliculas.domain.model.MovieSearchResult
import com.example.bitacoradepeliculas.domain.model.TmdbError
import com.example.bitacoradepeliculas.domain.model.toTmdbError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface MovieSearchRepository {
    suspend fun searchMovies(query: String): Result<List<MovieSearchResult>>
    suspend fun getDirector(tmdbId: Long): Result<String?>
}

class MovieSearchRepositoryImpl(
    private val httpClient: HttpClient,
) : MovieSearchRepository {

    private val directorCache = mutableMapOf<Long, String?>()

    override suspend fun searchMovies(query: String): Result<List<MovieSearchResult>> {
        if (AppConfig.TMDB_API_KEY.isBlank()) {
            return Result.failure(TmdbError.MissingApiKey)
        }

        return safeCall {
            val response: TmdbSearchResponse = httpClient.get("${AppConfig.TMDB_BASE_URL}/search/movie") {
                parameter("api_key", AppConfig.TMDB_API_KEY)
                parameter("query", query)
                parameter("language", "es-ES")
                parameter("include_adult", value = false)
                parameter("page", 1)
            }.body()

            response.results.take(10).map { dto ->
                val year = dto.releaseDate?.take(4)?.toIntOrNull()
                MovieSearchResult(
                    tmdbId = dto.id,
                    title = dto.title,
                    year = year,
                    posterPath = dto.posterPath,
                    directorState = DirectorState.Loading,
                )
            }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it.toTmdbError()) }
        )
    }

    override suspend fun getDirector(tmdbId: Long): Result<String?> {
        if (directorCache.containsKey(tmdbId)) {
            return Result.success(directorCache[tmdbId])
        }

        if (AppConfig.TMDB_API_KEY.isBlank()) {
            return Result.failure(TmdbError.MissingApiKey)
        }

        return safeCall {
            val response: TmdbCreditsResponse = httpClient.get("${AppConfig.TMDB_BASE_URL}/movie/$tmdbId/credits") {
                parameter("api_key", AppConfig.TMDB_API_KEY)
                parameter("language", "es-ES")
            }.body()

            val directors = response.crew
                .filter { it.job == "Director" }
                .mapNotNull { it.name }
                .distinct()

            val directorName = if (directors.isNotEmpty()) directors.joinToString(", ") else null
            directorCache[tmdbId] = directorName
            directorName
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it.toTmdbError()) }
        )
    }
}
