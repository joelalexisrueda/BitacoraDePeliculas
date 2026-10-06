package com.example.bitacoradepeliculas.di

import com.example.bitacoradepeliculas.config.AppConfig
import com.example.bitacoradepeliculas.data.repository.AuthRepository
import com.example.bitacoradepeliculas.data.repository.AuthRepositoryImpl
import com.example.bitacoradepeliculas.presentation.auth.LoginViewModel
import com.example.bitacoradepeliculas.presentation.auth.RegisterViewModel
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

    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}
