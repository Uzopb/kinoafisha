package org.example.kinoafisha.afisha.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.ic_grid
import kinoafisha.feature.afisha.generated.resources.ic_list
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun LayoutToggle(
    layout: FeedLayout,
    onLayoutChange: (FeedLayout) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    val shape = RoundedCornerShape(KinoRadii.Pill)
    Row(
        modifier = modifier
            .height(40.dp)
            .border(1.dp, colors.line, shape)
            .background(colors.bgSoft, shape)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LayoutBtn(
            icon = Res.drawable.ic_list,
            contentDescription = "Список",
            selected = layout == FeedLayout.List,
            onClick = { onLayoutChange(FeedLayout.List) },
        )
        LayoutBtn(
            icon = Res.drawable.ic_grid,
            contentDescription = "Сетка",
            selected = layout == FeedLayout.Grid,
            onClick = { onLayoutChange(FeedLayout.Grid) },
        )
    }
}

@Composable
private fun LayoutBtn(
    icon: DrawableResource,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = KinoTheme.colors
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (selected) colors.bgHover else colors.bgSoft.copy(alpha = 0f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(16.dp),
            tint = if (selected) KinoColors.Text else colors.textDim,
        )
    }
}
