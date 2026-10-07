package com.example.bitacoradepeliculas.di

import com.example.bitacoradepeliculas.config.AppConfig
import com.example.bitacoradepeliculas.data.repository.AuthRepository
import com.example.bitacoradepeliculas.data.repository.AuthRepositoryImpl
import com.example.bitacoradepeliculas.data.repository.MovieLogRepository
import com.example.bitacoradepeliculas.data.repository.MovieLogRepositoryImpl
import com.example.bitacoradepeliculas.presentation.auth.LoginViewModel
import com.example.bitacoradepeliculas.presentation.auth.RegisterViewModel
import com.example.bitacoradepeliculas.presentation.home.HomeViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.module.dsl.viewModelOf
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

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<MovieLogRepository> { MovieLogRepositoryImpl(get()) }

    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::HomeViewModel)
}
