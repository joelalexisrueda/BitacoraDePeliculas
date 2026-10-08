package com.example.bitacoradepeliculas.ui.log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bitacoradepeliculas.domain.util.RatingUtils
import com.example.bitacoradepeliculas.domain.util.SystemTodayProvider
import com.example.bitacoradepeliculas.domain.util.TodayProvider
import com.example.bitacoradepeliculas.presentation.log.LogMovieEvent
import com.example.bitacoradepeliculas.presentation.log.LogMovieViewModel
import com.example.bitacoradepeliculas.ui.components.AppIcons
import com.example.bitacoradepeliculas.ui.log.components.LogDateField
import com.example.bitacoradepeliculas.ui.log.components.MovieHeader
import com.example.bitacoradepeliculas.ui.log.components.ReviewTextField
import com.example.bitacoradepeliculas.ui.log.components.StarRatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogMovieScreen(
    viewModel: LogMovieViewModel,
    todayProvider: TodayProvider = remember { SystemTodayProvider() },
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LogMovieEvent.NavigateBack -> onNavigateBack()
                is LogMovieEvent.NavigateToHome -> onNavigateToHome()
                is LogMovieEvent.NavigateToLogin -> onNavigateToLogin()
                is LogMovieEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reseñar película") },
                navigationIcon = {
                    IconButton(
                        onClick = viewModel::onBackClicked,
                        enabled = !uiState.isSaving
                    ) {
                        Icon(
                            imageVector = AppIcons.Back,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = viewModel::saveMovieLog,
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = AppIcons.Check,
                                contentDescription = "Guardar reseña",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Movie Header (Poster + Title + Year)
                MovieHeader(
                    title = uiState.movieTitle,
                    year = uiState.movieYear,
                    posterPath = uiState.moviePosterPath
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Star Rating Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StarRatingBar(
                        score = uiState.score,
                        onScoreChanged = viewModel::onScoreChanged
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = RatingUtils.formatScoreText(uiState.score),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Date Picker Field
                LogDateField(
                    date = uiState.logDate,
                    todayProvider = todayProvider,
                    isDatePickerVisible = uiState.isDatePickerVisible,
                    onDatePickerVisibilityChanged = viewModel::onDatePickerVisibilityChanged,
                    onDateSelected = viewModel::onDateSelected,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Review Text Field
                ReviewTextField(
                    value = uiState.reviewText,
                    onValueChange = viewModel::onReviewTextChange,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
