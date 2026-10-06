package com.example.bitacoradepeliculas.config

/**
 * Configuración centralizada de la aplicación.
 *
 * Para entorno de desarrollo/producción, se recomienda configurar las credenciales
 * mediante variables de entorno o archivo local.properties sin versionar.
 */
object AppConfig {
    // Reemplaza con tu URL y Anon Key de Supabase o configúralos en local.properties
    const val SUPABASE_URL: String = "https://example.supabase.co"
    const val SUPABASE_ANON_KEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.example"
}
