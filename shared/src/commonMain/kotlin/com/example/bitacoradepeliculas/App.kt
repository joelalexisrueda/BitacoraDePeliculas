package com.example.bitacoradepeliculas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.bitacoradepeliculas.navigation.AppTransitions
import com.example.bitacoradepeliculas.navigation.Screen
import com.example.bitacoradepeliculas.presentation.auth.LoginViewModel
import com.example.bitacoradepeliculas.presentation.auth.RegisterViewModel
import com.example.bitacoradepeliculas.presentation.detail.EditMovieLogViewModel
import com.example.bitacoradepeliculas.presentation.detail.MovieLogDetailViewModel
import com.example.bitacoradepeliculas.presentation.home.HomeViewModel
import com.example.bitacoradepeliculas.presentation.log.LogMovieViewModel
import com.example.bitacoradepeliculas.presentation.search.SearchMovieViewModel
import com.example.bitacoradepeliculas.ui.auth.LoginScreen
import com.example.bitacoradepeliculas.ui.auth.RegisterScreen
import com.example.bitacoradepeliculas.ui.detail.EditMovieLogScreen
import com.example.bitacoradepeliculas.ui.detail.MovieLogDetailScreen
import com.example.bitacoradepeliculas.ui.home.HomeScreen
import com.example.bitacoradepeliculas.ui.log.LogMovieScreen
import com.example.bitacoradepeliculas.ui.search.SearchMovieScreen
import com.example.bitacoradepeliculas.ui.theme.AppTheme
import io.github.jan.supabase.auth.status.SessionStatus
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
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

                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = AppTransitions.sharedAxisEnter,
                    exitTransition = AppTransitions.sharedAxisExit,
                    popEnterTransition = AppTransitions.sharedAxisPopEnter,
                    popExitTransition = AppTransitions.sharedAxisPopExit
                ) {
                    composable<Screen.Login>(
                        enterTransition = AppTransitions.fadeThroughEnter,
                        exitTransition = AppTransitions.sharedAxisExit
                    ) {
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

                    composable<Screen.Home>(
                        enterTransition = AppTransitions.fadeThroughEnter
                    ) {
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
                        val viewModel: LogMovieViewModel = koinViewModel(
                            parameters = { parametersOf(route.title, route.year, route.posterPath) }
                        )
                        LogMovieScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToHome = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.MovieLogDetail> { backStackEntry ->
                        val route: Screen.MovieLogDetail = backStackEntry.toRoute()
                        val viewModel: MovieLogDetailViewModel = koinViewModel(
                            parameters = { parametersOf(route.movieLogId) }
                        )
                        MovieLogDetailScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToEdit = { id ->
                                navController.navigate(Screen.EditMovieLog(id))
                            },
                            onNavigateToHome = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable<Screen.EditMovieLog> { backStackEntry ->
                        val route: Screen.EditMovieLog = backStackEntry.toRoute()
                        val viewModel: EditMovieLogViewModel = koinViewModel(
                            parameters = { parametersOf(route.movieLogId) }
                        )
                        EditMovieLogScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToHome = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
