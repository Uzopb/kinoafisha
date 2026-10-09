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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.core.domain.model.FeedLayout

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
            label = "☰",
            selected = layout == FeedLayout.List,
            onClick = { onLayoutChange(FeedLayout.List) },
        )
        LayoutBtn(
            label = "▦",
            selected = layout == FeedLayout.Grid,
            onClick = { onLayoutChange(FeedLayout.Grid) },
        )
    }
}

@Composable
private fun LayoutBtn(
    label: String,
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
        Text(
            text = label,
            color = if (selected) KinoColors.Text else colors.textDim,
            fontSize = 14.sp,
        )
    }
}
