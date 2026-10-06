package com.example.bitacoradepeliculas

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform