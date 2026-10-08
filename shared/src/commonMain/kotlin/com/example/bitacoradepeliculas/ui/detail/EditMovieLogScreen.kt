package com.example.bitacoradepeliculas.ui.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bitacoradepeliculas.domain.util.RatingUtils
import com.example.bitacoradepeliculas.domain.util.SystemTodayProvider
import com.example.bitacoradepeliculas.domain.util.TodayProvider
import com.example.bitacoradepeliculas.domain.util.TmdbUtils
import com.example.bitacoradepeliculas.presentation.detail.EditMovieLogEvent
import com.example.bitacoradepeliculas.presentation.detail.EditMovieLogState
import com.example.bitacoradepeliculas.presentation.detail.EditMovieLogViewModel
import com.example.bitacoradepeliculas.ui.components.AppIcons
import com.example.bitacoradepeliculas.ui.log.components.LogDateField
import com.example.bitacoradepeliculas.ui.log.components.MovieHeader
import com.example.bitacoradepeliculas.ui.log.components.ReviewTextField
import com.example.bitacoradepeliculas.ui.log.components.StarRatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMovieLogScreen(
    viewModel: EditMovieLogViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    todayProvider: TodayProvider = remember { SystemTodayProvider() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditMovieLogEvent.NavigateBack -> onNavigateBack()
                is EditMovieLogEvent.NavigateToHome -> onNavigateToHome()
                is EditMovieLogEvent.NavigateToLogin -> onNavigateToLogin()
                is EditMovieLogEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar reseña") },
                navigationIcon = {
                    IconButton(
                        onClick = viewModel::onBackAttempt,
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
                        onClick = viewModel::saveChanges,
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
                                contentDescription = "Guardar cambios",
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
            AnimatedContent(
                targetState = uiState.editState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                },
                contentKey = { it::class },
                label = "EditMovieLogStateAnimation"
            ) { state ->
                when (state) {
                    is EditMovieLogState.Loading -> {
                        val infiniteTransition = rememberInfiniteTransition()
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 0.7f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 800),
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 600.dp)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .alpha(alpha)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .alpha(alpha)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }

                    is EditMovieLogState.NotFound -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = AppIcons.Movie,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Esta reseña ya no existe",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FilledTonalButton(onClick = onNavigateToHome) {
                                Text("Volver a Home")
                            }
                        }
                    }

                    is EditMovieLogState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FilledTonalButton(onClick = viewModel::retry) {
                                Text("Reintentar")
                            }
                        }
                    }

                    is EditMovieLogState.Editing -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 600.dp)
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MovieHeader(
                                title = state.movieTitle,
                                year = state.movieYear,
                                posterPath = state.moviePosterPath,
                                posterSize = TmdbUtils.DETAIL_POSTER_SIZE
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            // Star Rating Bar (Editable)
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

                            // Date Field
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

            // Discard Changes AlertDialog
            if (uiState.isDiscardDialogVisible) {
                AlertDialog(
                    onDismissRequest = viewModel::onDiscardDialogDismissed,
                    title = { Text("¿Descartar cambios?") },
                    text = { Text("Tienes cambios sin guardar. Si sales ahora, se perderán.") },
                    confirmButton = {
                        TextButton(
                            onClick = viewModel::confirmDiscard,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Descartar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = viewModel::onDiscardDialogDismissed
                        ) {
                            Text("Seguir editando")
                        }
                    }
                )
            }
        }
    }
}
