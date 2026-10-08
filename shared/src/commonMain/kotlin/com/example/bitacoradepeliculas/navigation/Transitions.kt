package com.example.bitacoradepeliculas.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry

/**
 * Constantes y funciones de transición centralizadas para la navegación.
 * Siguen el sistema de diseño de Material Motion:
 * - Shared Axis Horizontal (~30% de desplazamiento) para flujo hacia adelante/atrás.
 * - Fade Through para cambios de nivel superior (Splash -> Login/Home, Logout -> Login).
 */
object AppTransitions {
    const val STANDARD_DURATION = 300
    const val FADE_IN_DURATION = 210
    const val FADE_OUT_DURATION = 90

    // Curva cúbica enfatizada de Material
    val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    // Navegación hacia adelante (Shared Axis Horizontal)
    val sharedAxisEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> (fullWidth * 0.30f).toInt() },
            animationSpec = tween(STANDARD_DURATION, easing = EmphasizedEasing)
        ) + fadeIn(animationSpec = tween(STANDARD_DURATION, easing = EmphasizedEasing))
    }

    val sharedAxisExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> (-fullWidth * 0.30f).toInt() },
            animationSpec = tween(STANDARD_DURATION, easing = EmphasizedEasing)
        ) + fadeOut(animationSpec = tween(STANDARD_DURATION, easing = EmphasizedEasing))
    }

    // Navegación hacia atrás (Pop)
    val sharedAxisPopEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> (-fullWidth * 0.30f).toInt() },
            animationSpec = tween(STANDARD_DURATION, easing = EmphasizedEasing)
        ) + fadeIn(animationSpec = tween(STANDARD_DURATION, easing = EmphasizedEasing))
    }

    val sharedAxisPopExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> (fullWidth * 0.30f).toInt() },
            animationSpec = tween(STANDARD_DURATION, easing = EmphasizedEasing)
        ) + fadeOut(animationSpec = tween(STANDARD_DURATION, easing = EmphasizedEasing))
    }

    // Fade Through (Cambios de nivel superior / autenticación)
    val fadeThroughEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(
            animationSpec = tween(FADE_IN_DURATION, delayMillis = FADE_OUT_DURATION, easing = LinearOutSlowInEasing)
        ) + scaleIn(
            initialScale = 0.96f,
            animationSpec = tween(FADE_IN_DURATION, delayMillis = FADE_OUT_DURATION, easing = LinearOutSlowInEasing)
        )
    }

    val fadeThroughExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(
            animationSpec = tween(FADE_OUT_DURATION, easing = FastOutLinearInEasing)
        )
    }
}
