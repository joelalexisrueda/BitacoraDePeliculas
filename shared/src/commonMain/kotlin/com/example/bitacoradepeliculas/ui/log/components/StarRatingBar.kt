package com.example.bitacoradepeliculas.ui.log.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.bitacoradepeliculas.domain.util.RatingUtils
import com.example.bitacoradepeliculas.ui.components.AppIcons
import kotlin.math.round

@Composable
fun StarRatingBar(
    score: Double,
    onScoreChanged: (newScore: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val ratingDescription = "Puntuación ${RatingUtils.formatScoreText(score)}"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = ratingDescription }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val x = change.position.x
                    val totalWidth = size.width
                    if (totalWidth > 0) {
                        val fraction = (x / totalWidth).coerceIn(0f, 1f)
                        val rawScore = round(fraction * 20) * 0.5
                        val clamped = rawScore.coerceIn(0.0, 10.0)
                        onScoreChanged(clamped)
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..10) {
                val starValue = i.toDouble()
                val isFull = score >= starValue
                val isHalf = score >= starValue - 0.5 && score < starValue

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .widthIn(max = 36.dp)
                        .heightIn(max = 36.dp)
                        .pointerInput(i) {
                            detectTapGestures { offset ->
                                val isLeft = offset.x < size.width / 2
                                val newScore = RatingUtils.calculateScore(i, isLeft, score)
                                onScoreChanged(newScore)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when {
                        isFull -> AppIcons.Star
                        isHalf -> AppIcons.StarHalf
                        else -> AppIcons.Star
                    }
                    val tint = if (isFull || isHalf) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
