# Bitácora de Películas (Kotlin Multiplatform)

Aplicación móvil desarrollada en **Kotlin Multiplatform (KMP)** y **Compose Multiplatform** para registrar y calificar películas vistas, consumiendo la API de **TMDB** y utilizando **Supabase** como backend de autenticación y base de datos.

## Estructura del Proyecto

* [/shared](./shared/src) contiene el código común en Kotlin Multiplatform (módulos de datos, dominio, presentación MVVM y pantallas Compose).
* [/androidApp](./androidApp) aplicación Android.
* [/iosApp](./iosApp) aplicación iOS en SwiftUI/KMP.

## Configuración de Claves (local.properties)

Para habilitar la búsqueda de películas con la API pública de **TMDB**, debes agregar tu clave en el archivo `local.properties` ubicado en la raíz del proyecto (este archivo está ignorado por Git para proteger tus credenciales):

```properties
TMDB_API_KEY=tu_tmdb_api_key_aqui
```

> **Nota sobre seguridad de claves cliente:**
> En aplicaciones móviles cliente, las claves incluidas dentro de un APK o binario pueden ser extraídas mediante ingeniería inversa. Esto es aceptable y esperado para un challenge técnico o clientes públicos de TMDB. Para entornos productivos de alta seguridad, se recomienda utilizar un proxy/backend intermedio.

Si no se configura `TMDB_API_KEY` en `local.properties`, el proyecto **compilará exitosamente** (con una advertencia en la consola de Gradle), y en la aplicación la búsqueda mostrará un aviso en español: *"Falta configurar la clave de TMDB"*.

## Cómo compilar

- Compilación Android: `./gradlew :androidApp:assembleDebug`
- Ejecución de Pruebas Unitarias: `./gradlew :shared:allTests`
