package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.domain.model.MovieSearchResult
import com.example.bitacoradepeliculas.domain.model.TmdbError

class FakeMovieSearchRepository : MovieSearchRepository {

    val searchResults = mutableListOf<MovieSearchResult>()
    val directorsMap = mutableMapOf<Long, String?>()
    var shouldFailSearch = false
    var shouldFailDirector = false
    var searchError: TmdbError = TmdbError.NoInternet
    var directorError: TmdbError = TmdbError.ServerError

    var searchCallCount = 0
    var directorCallCountMap = mutableMapOf<Long, Int>()

    override suspend fun searchMovies(query: String): Result<List<MovieSearchResult>> {
        searchCallCount++
        return if (shouldFailSearch) {
            Result.failure(searchError)
        } else {
            val filtered = searchResults.filter { it.title.contains(query, ignoreCase = true) }
            Result.success(filtered)
        }
    }

    override suspend fun getDirector(tmdbId: Long): Result<String?> {
        directorCallCountMap[tmdbId] = (directorCallCountMap[tmdbId] ?: 0) + 1
        return if (shouldFailDirector) {
            Result.failure(directorError)
        } else {
            Result.success(directorsMap[tmdbId])
        }
    }
}
