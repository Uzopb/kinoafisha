package org.example.kinoafisha.afisha.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.example.kinoafisha.afisha.ui.components.FeedToolbar
import org.example.kinoafisha.afisha.ui.components.MovieFeed
import org.example.kinoafisha.afisha.vm.FeedViewModel
import org.example.kinoafisha.core.domain.model.Movie
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeedScreen(
    onOpenDetails: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    MovieFeed(
        items = state.items,
        favoriteIds = state.favoriteIds,
        layout = state.layout,
        loading = state.loading,
        error = state.error,
        emptyText = "По запросу ничего не найдено.",
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
                title = state.title,
                layout = state.layout,
                sort = state.sort,
                onLayoutChange = viewModel::setLayout,
                onSortChange = viewModel::setSort,
            )
        },
    )
}
