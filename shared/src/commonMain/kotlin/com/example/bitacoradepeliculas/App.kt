package com.example.bitacoradepeliculas

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bitacoradepeliculas.data.repository.AuthRepository
import com.example.bitacoradepeliculas.di.appModule
import com.example.bitacoradepeliculas.navigation.Screen
import com.example.bitacoradepeliculas.presentation.auth.LoginViewModel
import com.example.bitacoradepeliculas.presentation.auth.RegisterViewModel
import com.example.bitacoradepeliculas.presentation.home.HomeViewModel
import com.example.bitacoradepeliculas.presentation.search.SearchMovieViewModel
import com.example.bitacoradepeliculas.ui.auth.LoginScreen
import com.example.bitacoradepeliculas.ui.auth.RegisterScreen
import com.example.bitacoradepeliculas.ui.detail.MovieLogDetailScreen
import com.example.bitacoradepeliculas.ui.home.HomeScreen
import com.example.bitacoradepeliculas.ui.log.LogMovieScreen
import com.example.bitacoradepeliculas.ui.search.SearchMovieScreen
import com.example.bitacoradepeliculas.ui.theme.AppTheme
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

private fun initKoin() {
    if (KoinPlatform.getKoinOrNull() == null) {
        startKoin {
            modules(appModule)
        }
    }
}

@Composable
fun App() {
    initKoin()

    AppTheme {
        val authRepository: AuthRepository = koinInject()
        val sessionStatus by authRepository.sessionStatus.collectAsStateWithLifecycle()

        when (sessionStatus) {
            is SessionStatus.Initializing -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            else -> {
                val isAuthenticated = sessionStatus is SessionStatus.Authenticated
                val startDestination: Screen = if (isAuthenticated) Screen.Home else Screen.Login
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) {
                    composable<Screen.Login> {
                        val viewModel: LoginViewModel = koinViewModel()
                        LoginScreen(
                            viewModel = viewModel,
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register)
                            },
                            onLoginSuccess = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Login) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.Register> {
                        val viewModel: RegisterViewModel = koinViewModel()
                        RegisterScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onRegisterSuccess = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Register) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.Home> {
                        val viewModel: HomeViewModel = koinViewModel()
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            },
                            onNavigateToSearchMovie = {
                                navController.navigate(Screen.SearchMovie)
                            },
                            onNavigateToReviewDetail = { movieLogId ->
                                navController.navigate(Screen.MovieLogDetail(movieLogId))
                            }
                        )
                    }

                    composable<Screen.SearchMovie> {
                        val viewModel: SearchMovieViewModel = koinViewModel()
                        SearchMovieScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToLogMovie = { title, year, posterPath ->
                                navController.navigate(Screen.LogMovie(title, year, posterPath))
                            }
                        )
                    }

                    composable<Screen.LogMovie> { backStackEntry ->
                        val route: Screen.LogMovie = backStackEntry.toRoute()
                        LogMovieScreen(
                            movieTitle = route.title,
                            movieYear = route.year,
                            posterPath = route.posterPath,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<Screen.MovieLogDetail> { backStackEntry ->
                        val route: Screen.MovieLogDetail = backStackEntry.toRoute()
                        MovieLogDetailScreen(
                            movieLogId = route.movieLogId,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
