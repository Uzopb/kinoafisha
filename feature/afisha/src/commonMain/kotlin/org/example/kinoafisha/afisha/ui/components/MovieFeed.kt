package org.example.kinoafisha.afisha.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.ic_popcorn
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.ui.atoms.MovieCard
import org.example.kinoafisha.afisha.ui.atoms.MovieCardSkeleton
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.Movie
import org.jetbrains.compose.resources.painterResource

@Composable
fun MovieFeed(
    items: List<Movie>,
    favoriteIds: Set<Long>,
    layout: FeedLayout,
    loading: Boolean,
    error: String?,
    emptyText: String,
    onRetry: () -> Unit,
    onOpen: (Movie) -> Unit,
    onToggleFavorite: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    loadingMore: Boolean = false,
    hasMore: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    loadMoreFailed: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null,
) {
    val colors = KinoTheme.colors

    when {
        loading && items.isEmpty() -> {
            Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                header?.invoke()
                repeat(5) {
                    MovieCardSkeleton(
                        layout = layout,
                        modifier = Modifier.padding(bottom = 14.dp),
                    )
                }
            }
        }

        error != null && items.isEmpty() -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = error,
                        color = colors.textDim,
                        textAlign = TextAlign.Center,
                    )
                    TextButton(onClick = onRetry) {
                        Text("Повторить", color = colors.accent2, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        items.isEmpty() -> {
            Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                header?.invoke()
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (emptyContent != null) {
                        emptyContent()
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_popcorn),
                                contentDescription = null,
                                tint = colors.line,
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .size(48.dp),
                            )
                            Text(emptyText, color = colors.textDim, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        layout == FeedLayout.Grid -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 148.dp),
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (header != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) { header() }
                }
                items(items, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        isFavorite = movie.id in favoriteIds,
                        layout = FeedLayout.Grid,
                        onClick = { onOpen(movie) },
                        onToggleFavorite = { onToggleFavorite(movie) },
                    )
                }
                if (onLoadMore != null && (hasMore || loadingMore || loadMoreFailed)) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LoadMoreButton(
                            loading = loadingMore,
                            failed = loadMoreFailed,
                            onClick = onLoadMore,
                        )
                    }
                }
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (header != null) {
                    item { header() }
                }
                items(items, key = { it.id }) { movie ->
                    MovieCard(
                        movie = movie,
                        isFavorite = movie.id in favoriteIds,
                        layout = FeedLayout.List,
                        onClick = { onOpen(movie) },
                        onToggleFavorite = { onToggleFavorite(movie) },
                    )
                }
                if (onLoadMore != null && (hasMore || loadingMore || loadMoreFailed)) {
                    item {
                        LoadMoreButton(
                            loading = loadingMore,
                            failed = loadMoreFailed,
                            onClick = onLoadMore,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadMoreButton(
    loading: Boolean,
    failed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    Button(
        onClick = onClick,
        enabled = !loading,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 20.dp),
        shape = RoundedCornerShape(KinoRadii.Md),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.bgSoft,
            contentColor = KinoColors.Text,
            disabledContainerColor = colors.bgSoft,
            disabledContentColor = colors.textDim,
        ),
    ) {
        Text(
            text = when {
                loading -> "Загрузка…"
                failed -> "Не удалось · ещё раз"
                else -> "Дальше"
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 6.dp),
        )
    }
}
