package com.example.bitacoradepeliculas.ui.log.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.bitacoradepeliculas.domain.util.RatingUtils
import com.example.bitacoradepeliculas.ui.components.AppIcons

@Composable
fun StarRatingBar(
    score: Double,
    onScoreChanged: (starIndex: Int, isLeftHalf: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val ratingDescription = "Puntuación ${RatingUtils.formatScoreText(score)}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = ratingDescription },
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
                    .heightIn(max = 36.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Left half clickable area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .aspectRatio(0.5f)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onScoreChanged(i, true)
                            }
                    )
                    // Right half clickable area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .aspectRatio(0.5f)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onScoreChanged(i, false)
                            }
                    )
                }

                // Star Icon
                val tint = if (isFull || isHalf) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                }

                Icon(
                    imageVector = AppIcons.Star,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
