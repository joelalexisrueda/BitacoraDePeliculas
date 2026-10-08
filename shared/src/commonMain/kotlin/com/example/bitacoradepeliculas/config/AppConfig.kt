package com.example.bitacoradepeliculas.config

/**
 * Configuración centralizada de la aplicación.
 *
 * NOTA DE ARQUITECTURA / PRUEBA TÉCNICA (Puesto Junior):
 * Con el propósito exclusivo de facilitar la ejecución, evaluación y revisión inmediata de esta prueba técnica,
 * la API Key de TMDB y las credenciales de Supabase se exponen de manera deliberada en el código fuente.
 * En un entorno profesional de producción, las credenciales, tokens y claves de API jamás deben incluirse
 * en el código cliente (APK/IPA), debiendo gestionarse mediante un backend proxy o variables de entorno protegidas.
 */
object AppConfig {
    const val SUPABASE_URL: String = "https://psixjbwpjdmnhkaplipd.supabase.co"
    const val SUPABASE_ANON_KEY: String = "sb_publishable_W7g7eCgY5xd_g-V51CdPZg_1OZR_iCC"

    const val TMDB_BASE_URL: String = "https://api.themoviedb.org/3"
    
    // TMDB API Key expuesta a propósito para la facilitación de la prueba técnica Junior.
    val TMDB_API_KEY: String
        get() = TmdbConfigGenerated.apiKey.ifBlank { "tu_tmdb_api_key_aqui" }
}
