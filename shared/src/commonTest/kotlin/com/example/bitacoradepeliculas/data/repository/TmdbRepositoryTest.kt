package com.example.bitacoradepeliculas.data.repository

import com.example.bitacoradepeliculas.domain.model.TmdbError
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TmdbRepositoryTest {

    @Test
    fun searchMovies_parsesResponseAndLimitsTo10() = runTest {
        val jsonResponse = """
            {
                "page": 1,
                "results": [
                    {"id": 1, "title": "Movie 1", "release_date": "2021-05-10", "poster_path": "/p1.jpg"},
                    {"id": 2, "title": "Movie 2", "release_date": "1999-12-01", "poster_path": null},
                    {"id": 3, "title": "Movie 3", "release_date": "", "poster_path": "/p3.jpg"},
                    {"id": 4, "title": "Movie 4", "release_date": "2020", "poster_path": "/p4.jpg"},
                    {"id": 5, "title": "Movie 5"},
                    {"id": 6, "title": "Movie 6"},
                    {"id": 7, "title": "Movie 7"},
                    {"id": 8, "title": "Movie 8"},
                    {"id": 9, "title": "Movie 9"},
                    {"id": 10, "title": "Movie 10"},
                    {"id": 11, "title": "Movie 11"}
                ],
                "total_results": 11
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            respond(
                content = jsonResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repository = MovieSearchRepositoryImpl(httpClient)
        val result = repository.searchMovies("Movie")

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!
        assertEquals(10, list.size) // Limit 10
        assertEquals(2021, list[0].year)
        assertEquals(1999, list[1].year)
        assertNull(list[2].year) // empty release_date -> null
        assertEquals(2020, list[3].year)
        assertNull(list[4].year) // missing release_date -> null
    }

    @Test
    fun getDirector_parsesCrewAndCaches() = runTest {
        val jsonResponse = """
            {
                "id": 550,
                "crew": [
                    {"name": "David Fincher", "job": "Director"},
                    {"name": "Brad Pitt", "job": "Actor"}
                ]
            }
        """.trimIndent()

        var callCount = 0
        val mockEngine = MockEngine { request ->
            callCount++
            respond(
                content = jsonResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repository = MovieSearchRepositoryImpl(httpClient)
        
        // First call
        val dir1 = repository.getDirector(550L).getOrNull()
        assertEquals("David Fincher", dir1)
        assertEquals(1, callCount)

        // Second call -> cached, no new network request
        val dir2 = repository.getDirector(550L).getOrNull()
        assertEquals("David Fincher", dir2)
        assertEquals(1, callCount)
    }

    @Test
    fun searchMovies_http401_mapsToInvalidApiKeyError() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"status_message": "Invalid API key"}""",
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repository = MovieSearchRepositoryImpl(httpClient)
        val result = repository.searchMovies("Test")

        assertTrue(result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue(error is TmdbError.InvalidApiKey)
        assertEquals("Clave de API de TMDB no válida.", error.userMessage)
    }

    @Test
    fun searchMovies_http429_mapsToRateLimitedError() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """{"status_message": "Rate limit exceeded"}""",
                status = HttpStatusCode.TooManyRequests,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repository = MovieSearchRepositoryImpl(httpClient)
        val result = repository.searchMovies("Test")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is TmdbError.RateLimited)
    }
}
