package org.example.kinoafisha.afisha.ui.atoms

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.core.domain.model.FeedLayout

@Composable
fun MovieCardSkeleton(
    layout: FeedLayout,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    val shimmer = shimmerBrush()
    val shape = RoundedCornerShape(KinoRadii.Lg)

    if (layout == FeedLayout.Grid) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, colors.line, shape)
                .background(colors.bgCard)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(KinoRadii.Md))
                    .background(shimmer),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmer),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmer),
            )
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, colors.line, shape)
                .background(colors.bgCard)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(92.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(KinoRadii.Md))
                    .background(shimmer),
            )
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(17.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(shimmer),
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(shimmer),
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(shimmer),
                )
            }
        }
    }
}

@Composable
private fun shimmerBrush(): Brush {
    val colors = KinoTheme.colors
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerX",
    )
    return Brush.linearGradient(
        colors = listOf(colors.bgHover, colors.line, colors.bgHover),
        start = Offset(x - 200f, 0f),
        end = Offset(x, 0f),
    )
}
