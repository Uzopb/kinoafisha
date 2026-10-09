package org.example.kinoafisha.afisha.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.ic_clapperboard
import kinoafisha.feature.afisha.generated.resources.ic_heart
import kinoafisha.feature.afisha.generated.resources.ic_menu
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.ui.components.GenreDrawer
import org.example.kinoafisha.afisha.ui.components.SearchBar
import org.example.kinoafisha.afisha.ui.screens.DetailsScreen
import org.example.kinoafisha.afisha.ui.screens.FavoritesScreen
import org.example.kinoafisha.afisha.ui.screens.FeedScreen
import org.example.kinoafisha.afisha.vm.FeedViewModel
import org.example.kinoafisha.core.domain.model.Movie
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

enum class AfishaScreen { Feed, Favorites }

data class DetailsNav(
    val movieId: Long,
    val preview: Movie?,
)

@Composable
fun AfishaShell(
    feedViewModel: FeedViewModel = koinViewModel(),
) {
    val colors = KinoTheme.colors
    val feedState by feedViewModel.state.collectAsState()

    var screen by remember { mutableStateOf(AfishaScreen.Feed) }
    var details by remember { mutableStateOf<DetailsNav?>(null) }
    var prevScreen by remember { mutableStateOf(AfishaScreen.Feed) }
    var menuOpen by remember { mutableStateOf(false) }

    val showDetails = details != null
    val showSearch = !showDetails

    fun openDetails(movie: Movie) {
        if (!showDetails) prevScreen = screen
        details = DetailsNav(movie.id, movie)
    }

    fun selectScreen(next: AfishaScreen) {
        if (next == AfishaScreen.Feed &&
            (feedState.genreId != null || feedState.query.isNotBlank())
        ) {
            feedViewModel.resetFiltersAndRefresh()
        }
        details = null
        screen = next
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(KinoColors.Bg)) {
        val useBottomNav = maxWidth < 720.dp
        val bottomNavHeight = if (useBottomNav && !showDetails) 64.dp else 0.dp

        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(KinoColors.Bg.copy(alpha = 0.92f))
                    .border(width = 0.dp, color = colors.line)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BurgerButton(onClick = { menuOpen = true })

                if (!useBottomNav) {
                    TopNavLink(
                        label = "Новинки",
                        selected = !showDetails && screen == AfishaScreen.Feed,
                        onClick = { selectScreen(AfishaScreen.Feed) },
                    )
                    TopNavLink(
                        label = "Избранное",
                        selected = !showDetails && screen == AfishaScreen.Favorites,
                        onClick = { selectScreen(AfishaScreen.Favorites) },
                    )
                }

                if (showSearch) {
                    SearchBar(
                        query = feedState.query,
                        onQueryChange = {
                            feedViewModel.onQueryChange(it)
                            if (screen != AfishaScreen.Feed) screen = AfishaScreen.Feed
                            details = null
                        },
                        onClear = {
                            feedViewModel.clearQuery()
                            if (screen != AfishaScreen.Feed) screen = AfishaScreen.Feed
                        },
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(max = 320.dp),
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }

            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = bottomNavHeight),
            ) {
                Box(Modifier.widthIn(max = 960.dp).fillMaxSize().align(Alignment.TopCenter)) {
                    when {
                        showDetails -> {
                            val nav = details!!
                            DetailsScreen(
                                movieId = nav.movieId,
                                preview = nav.preview,
                                onBack = { details = null; screen = prevScreen },
                            )
                        }

                        screen == AfishaScreen.Favorites -> {
                            FavoritesScreen(onOpenDetails = ::openDetails)
                        }

                        else -> {
                            FeedScreen(
                                onOpenDetails = ::openDetails,
                                viewModel = feedViewModel,
                            )
                        }
                    }
                }
            }
        }

        if (useBottomNav && !showDetails) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(colors.bgSoft.copy(alpha = 0.95f)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BottomNavItem(
                    label = "Новинки",
                    icon = Res.drawable.ic_clapperboard,
                    selected = screen == AfishaScreen.Feed,
                    onClick = { selectScreen(AfishaScreen.Feed) },
                    modifier = Modifier.weight(1f),
                )
                BottomNavItem(
                    label = "Избранное",
                    icon = Res.drawable.ic_heart,
                    selected = screen == AfishaScreen.Favorites,
                    onClick = { selectScreen(AfishaScreen.Favorites) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        GenreDrawer(
            open = menuOpen,
            genres = feedState.genres,
            selectedGenreId = feedState.genreId,
            onSelect = { id ->
                feedViewModel.selectGenre(id)
                screen = AfishaScreen.Feed
                details = null
            },
            onClose = { menuOpen = false },
        )
    }
}

@Composable
private fun BurgerButton(onClick: () -> Unit) {
    val colors = KinoTheme.colors
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, colors.line, RoundedCornerShape(10.dp))
            .background(colors.bgSoft)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_menu),
            contentDescription = "Меню",
            tint = KinoColors.Text,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun TopNavLink(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = KinoTheme.colors
    Text(
        text = label,
        color = if (selected) KinoColors.Text else colors.textDim,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) colors.bgHover else colors.bgSoft.copy(alpha = 0f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: DrawableResource,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    val tint = if (selected) colors.accent2 else colors.textDim
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Text(label, color = tint, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
