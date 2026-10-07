package com.example.bitacoradepeliculas.config

/**
 * Configuración centralizada de la aplicación.
 *
 * - Credenciales de Supabase: URL y Anon Key (públicas por diseño).
 * - TMDB API Key: Se lee dinámicamente desde 'local.properties' mediante la tarea de Gradle
 *   generateTmdbConfig y se inyecta en el archivo generado TmdbConfigGenerated.
 */
object AppConfig {
    const val SUPABASE_URL: String = "https://psixjbwpjdmnhkaplipd.supabase.co"
    const val SUPABASE_ANON_KEY: String = "sb_publishable_W7g7eCgY5xd_g-V51CdPZg_1OZR_iCC"

    const val TMDB_BASE_URL: String = "https://api.themoviedb.org/3"
    val TMDB_API_KEY: String get() = TmdbConfigGenerated.apiKey
}
