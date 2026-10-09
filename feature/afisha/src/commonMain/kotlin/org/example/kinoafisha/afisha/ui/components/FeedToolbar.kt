package org.example.kinoafisha.afisha.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.ic_film_roll
import kinoafisha.feature.afisha.generated.resources.ic_heart
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.SortOrder
import org.jetbrains.compose.resources.painterResource

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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
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
        )
        Spacer(Modifier.weight(1f))
        LayoutToggle(layout = layout, onLayoutChange = onLayoutChange)
        SortDropdown(
            sort = sort,
            onSortChange = onSortChange,
            forFavorites = forFavorites,
        )
    }
}
