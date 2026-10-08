# Bitácora de Películas (Kotlin Multiplatform)

Aplicación móvil desarrollada en **Kotlin Multiplatform (KMP)** y **Compose Multiplatform** como solución al challenge técnico para un puesto de **Junior Mobile Software Engineer**. La aplicación permite llevar un registro personal de películas vistas, calificarlas con un sistema de estrellas y medias estrellas, buscar películas en tiempo real mediante la API de **TMDB** y sincronizar los datos de forma segura en la nube con **Supabase** (Autenticación y Base de Datos con RLS).

---

## 🚀 Características Principales (Pantallas 1 a 7)

1. **Autenticación (Login y Registro):** 
   - Inicio de sesión y creación de cuentas conectados a Supabase Auth.
   - Validación de formularios reactiva con `AuthValidator` (incluyendo límite de 15 caracteres en el nombre con protección de pares suplentes/emojis).
2. **Home (Listado de Películas):**
   - Muestra la bitácora personal de películas del usuario con actualización automática al retomar la pantalla (`onResumed`).
   - Soporte de *pull-to-refresh*, estado vacío con diseño amigable y transiciones fluidas con `AnimatedContent`.
3. **Búsqueda de Películas en TMDB:**
   - Barra de búsqueda con *debounce* de 500ms y normalización de consultas.
   - Carga concurrente y asíncrona de los directores de cada película con limitación de concurrencia mediante `Semaphore`.
4. **Registro de Película (Pantalla 5):**
   - Sistema interactivo de 10 estrellas con soporte para medias estrellas (0.5 en 0.5), tanto por toques individuales como por gestos de arrastre (*slide*).
   - Selector de fecha validado para impedir fechas futuras.
   - Cuadro de texto para reseñas con límite de 1000 caracteres y contador en tiempo real.
5. **Detalle y Edición de Registros (Pantallas 6 y 7):**
   - Vista de detalle con póster en alta resolución (`w500`), fecha formateada en español y puntaje.
   - Edición completa con detección automática de cambios sin guardar (`isDirty`) y diálogo de confirmación para descartar.
   - Eliminación de registros con diálogo de confirmación idempotente.

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** Kotlin 2.1+ (Multiplatform).
- **UI:** Compose Multiplatform (Material 3) con transiciones suaves estilo *Material Motion*.
- **Arquitectura:** MVVM (Model-View-ViewModel) con flujos inmutables (`StateFlow`) y eventos de una sola vez (`Channel`).
- **Inyección de Dependencias:** Koin.
- **Red y Serialización:** Ktor Client y `kotlinx-serialization`.
- **Backend & Base de Datos:** Supabase Kotlin SDK (`auth-kt` y `postgrest-kt`) con seguridad Row Level Security (RLS).
- **Carga de Imágenes:** Coil 3 (`AsyncImage` con red Ktor).
- **Pruebas Unitarias:** 92 pruebas unitarias en `commonTest` utilizando `kotlinx-coroutines-test` (fakes configurables y aserciones concretas).

---

## ⚠️ Nota sobre Credenciales y Prueba Técnica

> **Aviso de arquitectura para evaluación:**
> Con el propósito exclusivo de facilitar la ejecución, evaluación y revisión inmediata de esta prueba técnica (puesto Junior), las credenciales de conexión (como la URL de Supabase y las API Keys públicas) se encuentran expuestas de manera directa en el código de configuración (`AppConfig.kt`). 
> 
> En un **entorno profesional de producción**, los secretos, tokens y claves de API jamás deben incluirse en el código fuente del cliente (APK/IPA), ya que pueden ser extraídos mediante ingeniería inversa. Deben gestionarse mediante variables de entorno seguras, almacenamiento protegido en el dispositivo (*EncryptedSharedPreferences*) o a través de un servidor backend intermedio (*proxy*).

---

## 🧪 Ejecución de Pruebas Unitarias

El proyecto cuenta con una robusta suite de pruebas unitarias que cubren ViewModels, validaciones, utilidades puras y repositorios:

```bash
./gradlew :shared:allTests
```

## 📦 Compilación

- **Android Debug:** `./gradlew :androidApp:assembleDebug`
- **Android Release (con minificación R8 y reducción de recursos):** `./gradlew :androidApp:assembleRelease`
