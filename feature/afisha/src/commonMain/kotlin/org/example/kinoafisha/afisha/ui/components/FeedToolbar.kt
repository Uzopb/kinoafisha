package org.example.kinoafisha.afisha.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.cd_sort
import kinoafisha.feature.afisha.generated.resources.ic_film_roll
import kinoafisha.feature.afisha.generated.resources.ic_heart
import kinoafisha.feature.afisha.generated.resources.ic_sort
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.util.directionIcon
import org.example.kinoafisha.afisha.util.labelRes
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.SortOrder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val FadeAnim = tween<Float>(durationMillis = 280, easing = FastOutSlowInEasing)
private val SizeAnim = tween<IntSize>(durationMillis = 280, easing = FastOutSlowInEasing)

@Composable
fun FeedToolbar(
    title: String,
    layout: FeedLayout,
    sort: SortOrder,
    onLayoutChange: (FeedLayout) -> Unit,
    onSortChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
    forFavorites: Boolean = false,
) {
    val colors = KinoTheme.colors
    var sortExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = painterResource(
                    if (forFavorites) Res.drawable.ic_heart else Res.drawable.ic_film_roll,
                ),
                contentDescription = null,
                tint = colors.accent2,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = title,
                color = KinoColors.Text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            ToolbarControls(
                layout = layout,
                sortExpanded = sortExpanded,
                onLayoutChange = onLayoutChange,
                onSortToggle = { sortExpanded = !sortExpanded },
            )
        }

        AnimatedVisibility(
            visible = sortExpanded,
            enter = expandVertically(
                animationSpec = SizeAnim,
                expandFrom = Alignment.Top,
            ) + fadeIn(animationSpec = FadeAnim),
            exit = shrinkVertically(
                animationSpec = SizeAnim,
                shrinkTowards = Alignment.Top,
            ) + fadeOut(animationSpec = FadeAnim),
        ) {
            SortFilterRow(
                sort = sort,
                onSortChange = { option ->
                    onSortChange(option)
                    sortExpanded = false
                },
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Composable
private fun ToolbarControls(
    layout: FeedLayout,
    sortExpanded: Boolean,
    onLayoutChange: (FeedLayout) -> Unit,
    onSortToggle: () -> Unit,
) {
    val colors = KinoTheme.colors
    val shape = RoundedCornerShape(KinoRadii.Pill)
    Row(
        modifier = Modifier
            .height(40.dp)
            .border(1.dp, colors.line, shape)
            .background(colors.bgSoft, shape)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SortToggleButton(
            expanded = sortExpanded,
            onClick = onSortToggle,
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .width(1.dp)
                .height(18.dp)
                .background(colors.line),
        )
        LayoutToggle(
            layout = layout,
            onLayoutChange = onLayoutChange,
            bordered = false,
        )
    }
}

@Composable
private fun SortToggleButton(
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val colors = KinoTheme.colors
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (expanded) colors.bgHover else colors.bgSoft.copy(alpha = 0f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_sort),
            contentDescription = stringResource(Res.string.cd_sort),
            modifier = Modifier.size(16.dp),
            tint = if (expanded) colors.accent2 else colors.textDim,
        )
    }
}

@Composable
private fun SortFilterRow(
    sort: SortOrder,
    onSortChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SortOrder.entries.forEach { option ->
            SortFilterChip(
                option = option,
                selected = option == sort,
                onClick = { onSortChange(option) },
            )
        }
    }
}

@Composable
private fun SortFilterChip(
    option: SortOrder,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = KinoTheme.colors
    val shape = RoundedCornerShape(KinoRadii.Pill)
    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(shape)
            .border(1.dp, if (selected) colors.accent2.copy(alpha = 0.55f) else colors.line, shape)
            .background(if (selected) colors.bgHover else colors.bgSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(option.labelRes()),
            color = if (selected) KinoColors.Text else colors.textDim,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
        option.directionIcon()?.let { dir ->
            Icon(
                painter = painterResource(dir),
                contentDescription = null,
                tint = if (selected) colors.accent2 else colors.textDim,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}
