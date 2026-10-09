package org.example.kinoafisha.afisha.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.error_load
import kinoafisha.feature.afisha.generated.resources.feed_empty
import kinoafisha.feature.afisha.generated.resources.nav_new
import org.example.kinoafisha.afisha.ui.components.FeedToolbar
import org.example.kinoafisha.afisha.ui.components.MovieFeed
import org.example.kinoafisha.afisha.vm.FeedViewModel
import org.example.kinoafisha.core.domain.model.Movie
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedScreen(
    onOpenDetails: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val defaultTitle = stringResource(Res.string.nav_new)
    val title = state.genres.find { it.id == state.genreId }?.name
        ?.replaceFirstChar { it.uppercase() }
        ?: defaultTitle

    MovieFeed(
        items = state.items,
        favoriteIds = state.favoriteIds,
        layout = state.layout,
        loading = state.loading,
        error = if (state.hasError) stringResource(Res.string.error_load) else null,
        emptyText = stringResource(Res.string.feed_empty),
        onRetry = viewModel::retry,
        onOpen = onOpenDetails,
        onToggleFavorite = viewModel::toggleFav,
        modifier = modifier,
        loadingMore = state.loadingMore,
        hasMore = state.hasMore || state.loadMoreError,
        onLoadMore = viewModel::loadMore,
        loadMoreFailed = state.loadMoreError,
        header = {
            FeedToolbar(
                title = title,
                layout = state.layout,
                sort = state.sort,
                onLayoutChange = viewModel::setLayout,
                onSortChange = viewModel::setSort,
            )
        },
    )
}
