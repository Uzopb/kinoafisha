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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.cd_favorite_add
import kinoafisha.feature.afisha.generated.resources.cd_favorite_remove
import kinoafisha.feature.afisha.generated.resources.ic_heart
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
            painter = painterResource(Res.drawable.ic_heart),
            contentDescription = stringResource(
                if (isFavorite) Res.string.cd_favorite_remove else Res.string.cd_favorite_add,
            ),
            modifier = Modifier.size(iconSize),
            tint = if (isFavorite) KinoColors.OnAccent else colors.textDim,
        )
    }
}
