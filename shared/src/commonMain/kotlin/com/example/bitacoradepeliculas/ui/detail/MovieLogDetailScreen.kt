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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.OutlinedButton
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bitacoradepeliculas.domain.util.DateFormatter
import com.example.bitacoradepeliculas.domain.util.RatingUtils
import com.example.bitacoradepeliculas.domain.util.ReviewDisplayUtils
import com.example.bitacoradepeliculas.domain.util.TmdbUtils
import com.example.bitacoradepeliculas.presentation.detail.MovieLogDetailEvent
import com.example.bitacoradepeliculas.presentation.detail.MovieLogDetailState
import com.example.bitacoradepeliculas.presentation.detail.MovieLogDetailViewModel
import com.example.bitacoradepeliculas.ui.components.AppIcons
import com.example.bitacoradepeliculas.ui.log.components.MovieHeader
import com.example.bitacoradepeliculas.ui.log.components.StarRatingBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieLogDetailScreen(
    viewModel: MovieLogDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onResumed()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MovieLogDetailEvent.NavigateBack -> onNavigateBack()
                is MovieLogDetailEvent.NavigateToEdit -> onNavigateToEdit(event.movieLogId)
                is MovieLogDetailEvent.NavigateToHome -> onNavigateToHome()
                is MovieLogDetailEvent.NavigateToLogin -> onNavigateToLogin()
                is MovieLogDetailEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi reseña") },
                navigationIcon = {
                    IconButton(onClick = viewModel::onBackClicked) {
                        Icon(
                            imageVector = AppIcons.Back,
                            contentDescription = "Volver"
                        )
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
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedContent(
                targetState = uiState.detailState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                },
                contentKey = { it::class },
                label = "MovieLogDetailStateAnimation"
            ) { state ->
                when (state) {
                    is MovieLogDetailState.Loading -> {
                        // Skeleton loader with alpha animation
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
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .height(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .alpha(alpha)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }

                    is MovieLogDetailState.NotFound -> {
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

                    is MovieLogDetailState.Error -> {
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

                    is MovieLogDetailState.Content -> {
                        val movieLog = state.movieLog
                        val formattedDate = DateFormatter.formatToSpanish(movieLog.logDate)
                        val reviewText = ReviewDisplayUtils.formatReviewText(movieLog.reviewText)

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 600.dp)
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Movie Header with larger poster (w500)
                            MovieHeader(
                                title = movieLog.movieTitle,
                                year = movieLog.movieYear,
                                posterPath = movieLog.moviePosterPath,
                                posterSize = TmdbUtils.DETAIL_POSTER_SIZE
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Date Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = AppIcons.Calendar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Vista el $formattedDate",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Rating Bar (Read-only) + Score text
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                StarRatingBar(
                                    score = movieLog.score,
                                    readOnly = true
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = RatingUtils.formatScoreText(movieLog.score),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Review Text Box / Section
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Reseña",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                if (reviewText != null) {
                                    Text(
                                        text = reviewText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                } else {
                                    Text(
                                        text = "No escribiste una reseña para esta película",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(36.dp))

                            // Action Buttons: Edit and Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = viewModel::onEditClicked,
                                    enabled = !uiState.isDeleting,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = AppIcons.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Editar reseña")
                                }

                                OutlinedButton(
                                    onClick = viewModel::onDeleteClicked,
                                    enabled = !uiState.isDeleting,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    if (uiState.isDeleting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = MaterialTheme.colorScheme.error,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = AppIcons.Delete,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Confirmation Delete AlertDialog
            if (uiState.isDeleteDialogVisible) {
                val currentMovieLog = (uiState.detailState as? MovieLogDetailState.Content)?.movieLog
                val movieTitle = currentMovieLog?.movieTitle ?: "esta película"

                AlertDialog(
                    onDismissRequest = {
                        if (!uiState.isDeleting) {
                            viewModel.onDeleteDismissed()
                        }
                    },
                    title = { Text("¿Eliminar esta reseña?") },
                    text = { Text("Se eliminará el registro de «$movieTitle». Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(
                            onClick = viewModel::confirmDelete,
                            enabled = !uiState.isDeleting,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            if (uiState.isDeleting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Eliminar")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = viewModel::onDeleteDismissed,
                            enabled = !uiState.isDeleting
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
