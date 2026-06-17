package com.attyran.flickrsearch

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val highlightColor = MaterialTheme.colorScheme.surface
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translate - 300f, 0f),
        end = Offset(translate, 0f)
    )

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(brush)
    )
}

@Composable
fun ShimmerGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    itemSize: Dp = 180.dp,
    itemPadding: Dp = 4.dp,
) {
    val rowHeight = itemSize + itemPadding * 2

    BoxWithConstraints(modifier = modifier) {
        val itemCount = remember(maxHeight, rowHeight, columns) {
            val rows = ceil(maxHeight / rowHeight).toInt().coerceAtLeast(1)
            rows * columns
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false,
        ) {
            items(itemCount) {
                ShimmerBox(
                    modifier = Modifier
                        .padding(itemPadding)
                        .size(itemSize)
                )
            }
        }
    }
}
