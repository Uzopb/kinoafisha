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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.cd_clear
import kinoafisha.feature.afisha.generated.resources.cd_search_modes
import kinoafisha.feature.afisha.generated.resources.ic_chevron_down
import kinoafisha.feature.afisha.generated.resources.ic_close
import kinoafisha.feature.afisha.generated.resources.ic_search
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.util.labelRes
import org.example.kinoafisha.afisha.util.placeholderRes
import org.example.kinoafisha.core.domain.model.SearchMode
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val FadeAnim = tween<Float>(durationMillis = 280, easing = FastOutSlowInEasing)
private val SizeAnim = tween<IntSize>(durationMillis = 280, easing = FastOutSlowInEasing)

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    mode: SearchMode,
    onModeChange: (SearchMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    var modesExpanded by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(KinoRadii.Pill)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .border(1.dp, colors.line, shape)
                .background(colors.bgSoft, shape)
                .padding(start = 6.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchModeToggle(
                expanded = modesExpanded,
                onClick = { modesExpanded = !modesExpanded },
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                cursorBrush = SolidColor(colors.accent),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = KinoColors.Text,
                    fontSize = 14.sp,
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            stringResource(mode.placeholderRes()),
                            color = colors.textDim,
                            fontSize = 14.sp,
                            maxLines = 1,
                        )
                    }
                    inner()
                },
            )
            if (query.isNotEmpty()) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = stringResource(Res.string.cd_clear),
                    tint = colors.textDim,
                    modifier = Modifier
                        .clickable(onClick = onClear)
                        .padding(4.dp)
                        .size(14.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = modesExpanded,
            enter = expandVertically(
                animationSpec = SizeAnim,
                expandFrom = Alignment.Top,
            ) + fadeIn(animationSpec = FadeAnim),
            exit = shrinkVertically(
                animationSpec = SizeAnim,
                shrinkTowards = Alignment.Top,
            ) + fadeOut(animationSpec = FadeAnim),
        ) {
            SearchModeRow(
                mode = mode,
                onModeChange = { option ->
                    onModeChange(option)
                    modesExpanded = false
                },
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Composable
private fun SearchModeToggle(
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val colors = KinoTheme.colors
    val tint = if (expanded) colors.accent2 else colors.textDim
    Row(
        modifier = Modifier
            .height(28.dp)
            .clip(RoundedCornerShape(KinoRadii.Pill))
            .background(if (expanded) colors.bgHover else colors.bgSoft.copy(alpha = 0f))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = stringResource(Res.string.cd_search_modes),
            tint = tint,
            modifier = Modifier.size(16.dp),
        )
        Icon(
            painter = painterResource(Res.drawable.ic_chevron_down),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(12.dp),
        )
    }
}

@Composable
private fun SearchModeRow(
    mode: SearchMode,
    onModeChange: (SearchMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchMode.entries.forEach { option ->
            SearchModeChip(
                option = option,
                selected = option == mode,
                onClick = { onModeChange(option) },
            )
        }
    }
}

@Composable
private fun SearchModeChip(
    option: SearchMode,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = KinoTheme.colors
    val shape = RoundedCornerShape(KinoRadii.Pill)
    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(shape)
            .border(1.dp, if (selected) colors.accent2.copy(alpha = 0.55f) else colors.line, shape)
            .background(if (selected) colors.bgHover else colors.bgSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(option.labelRes()),
            color = if (selected) KinoColors.Text else colors.textDim,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
        )
    }
}
