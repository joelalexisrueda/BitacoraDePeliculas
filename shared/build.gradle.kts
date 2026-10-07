import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

abstract class GenerateTmdbConfigTask : DefaultTask() {

    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val localPropertiesFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val props = Properties()
        val file = localPropertiesFile.orNull?.asFile
        if (file != null && file.exists()) {
            file.inputStream().use { props.load(it) }
        }

        val apiKey = props.getProperty("TMDB_API_KEY", "").trim()
        if (apiKey.isBlank()) {
            logger.warn("⚠️ TMDB_API_KEY no se encontró o está vacía en local.properties. La búsqueda de películas mostrará un aviso de clave no configurada.")
        }

        val destinationDir = outputDir.get().asFile.resolve("com/example/bitacoradepeliculas/config")
        destinationDir.mkdirs()

        val generatedFile = destinationDir.resolve("TmdbConfigGenerated.kt")
        generatedFile.writeText(
            """
            package com.example.bitacoradepeliculas.config

            internal object TmdbConfigGenerated {
                val apiKey: String = "$apiKey"
            }
            """.trimIndent()
        )
    }
}

val generatedTmdbConfigDir = layout.buildDirectory.dir("generated/source/tmdb/commonMain")

val generateTmdbConfig = tasks.register<GenerateTmdbConfigTask>("generateTmdbConfig") {
    localPropertiesFile.set(rootProject.file("local.properties"))
    outputDir.set(generatedTmdbConfigDir)
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    dependsOn(generateTmdbConfig)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    android {
       namespace = "com.example.bitacoradepeliculas.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        commonMain {
            kotlin.srcDir(generateTmdbConfig)
            dependencies {
                // Compose Multiplatform
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)

                // 1. Navegación
                implementation(libs.navigation.compose)

                // 6. ViewModel y Lifecycle Multiplataforma
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // 2. Koin (DI)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                // 3. Ktor Client
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)

                // 4. Supabase-kt
                implementation(project.dependencies.platform(libs.supabase.bom))
                implementation(libs.supabase.auth)
                implementation(libs.supabase.postgrest)

                // 5. Coil3 (Carga de imágenes)
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor3)

                // 7. Complementos kotlinx
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}