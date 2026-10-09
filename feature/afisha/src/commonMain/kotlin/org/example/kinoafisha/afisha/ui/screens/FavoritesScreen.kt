package org.example.kinoafisha.afisha.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.ic_heart
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.ui.components.FeedToolbar
import org.example.kinoafisha.afisha.ui.components.MovieFeed
import org.example.kinoafisha.afisha.vm.FavoritesViewModel
import org.example.kinoafisha.core.domain.model.Movie
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    onOpenDetails: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val colors = KinoTheme.colors

    MovieFeed(
        items = state.items,
        favoriteIds = state.favoriteIds,
        layout = state.layout,
        loading = false,
        error = null,
        emptyText = "Пока пусто.",
        onRetry = {},
        onOpen = onOpenDetails,
        onToggleFavorite = viewModel::toggleFav,
        modifier = modifier,
        header = {
            FeedToolbar(
                title = "Избранное",
                layout = state.layout,
                sort = state.sort,
                onLayoutChange = viewModel::setLayout,
                onSortChange = viewModel::setSort,
                forFavorites = true,
            )
        },
        emptyContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(Res.drawable.ic_heart),
                    contentDescription = null,
                    tint = colors.line,
                    modifier = Modifier.size(52.dp).padding(bottom = 10.dp),
                )
                Text(
                    text = "Пока пусто. Отмечайте понравившееся сердечком — оно появится здесь.",
                    color = colors.textDim,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }
        },
    )
}
