package org.example.kinoafisha.afisha.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoTheme

@Composable
fun FavButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    iconSize: Dp = 18.dp,
) {
    val colors = KinoTheme.colors
    val bg = if (isFavorite) {
        Brush.linearGradient(listOf(colors.accent, colors.accent2))
    } else {
        Brush.linearGradient(listOf(colors.bgSoft, colors.bgSoft))
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bg)
            .then(
                if (isFavorite) {
                    Modifier
                } else {
                    Modifier.border(1.dp, colors.line, CircleShape)
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = HeartIcon,
            contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
            modifier = Modifier.size(iconSize),
            tint = if (isFavorite) KinoColors.OnAccent else colors.textDim,
        )
    }
}

val HeartIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Heart",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(12f, 21f)
            curveToRelative(0f, 0f, -8f, -4.7f, -10.5f, -9.5f)
            curveTo(-0.3f, 7.9f, 2.3f, 4f, 6.2f, 4f)
            curveToRelative(2.3f, 0f, 3.9f, 1.3f, 5.8f, 3.4f)
            curveTo(13.9f, 5.3f, 15.5f, 4f, 17.8f, 4f)
            curveToRelative(3.9f, 0f, 6.5f, 3.9f, 4.7f, 7.5f)
            curveTo(20f, 16.3f, 12f, 21f, 12f, 21f)
            close()
        }
    }.build()
}
