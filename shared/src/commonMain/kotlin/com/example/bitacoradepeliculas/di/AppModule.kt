package com.example.bitacoradepeliculas.di

import com.example.bitacoradepeliculas.config.AppConfig
import com.example.bitacoradepeliculas.data.repository.AuthRepository
import com.example.bitacoradepeliculas.data.repository.AuthRepositoryImpl
import com.example.bitacoradepeliculas.data.repository.MovieLogRepository
import com.example.bitacoradepeliculas.data.repository.MovieLogRepositoryImpl
import com.example.bitacoradepeliculas.data.repository.MovieSearchRepository
import com.example.bitacoradepeliculas.data.repository.MovieSearchRepositoryImpl
import com.example.bitacoradepeliculas.domain.util.SystemTodayProvider
import com.example.bitacoradepeliculas.domain.util.TodayProvider
import com.example.bitacoradepeliculas.presentation.auth.LoginViewModel
import com.example.bitacoradepeliculas.presentation.auth.RegisterViewModel
import com.example.bitacoradepeliculas.presentation.detail.MovieLogDetailViewModel
import com.example.bitacoradepeliculas.presentation.home.HomeViewModel
import com.example.bitacoradepeliculas.presentation.log.LogMovieViewModel
import com.example.bitacoradepeliculas.presentation.search.SearchMovieViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = AppConfig.SUPABASE_URL,
            supabaseKey = AppConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    // Cliente Ktor específico para la API de TMDB (Sin plugin de logging para proteger la API key en logs)
    single<HttpClient>(named("tmdbHttpClient")) {
        HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }
        }
    }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<MovieLogRepository> { MovieLogRepositoryImpl(get()) }
    single<MovieSearchRepository> { MovieSearchRepositoryImpl(get(named("tmdbHttpClient"))) }
    single<TodayProvider> { SystemTodayProvider() }

    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchMovieViewModel)
    viewModel { (title: String, year: Int?, posterPath: String?) ->
        LogMovieViewModel(get(), get(), title, year, posterPath)
    }
    viewModel { (movieLogId: Long) ->
        MovieLogDetailViewModel(get(), movieLogId)
    }
}
