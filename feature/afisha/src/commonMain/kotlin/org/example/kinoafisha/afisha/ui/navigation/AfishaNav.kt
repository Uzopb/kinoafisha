package org.example.kinoafisha.afisha.ui.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.action_back
import kinoafisha.feature.afisha.generated.resources.cd_menu
import kinoafisha.feature.afisha.generated.resources.ic_clapperboard
import kinoafisha.feature.afisha.generated.resources.ic_heart
import kinoafisha.feature.afisha.generated.resources.nav_favorites
import kinoafisha.feature.afisha.generated.resources.nav_new
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.ui.NavBarPadding
import org.example.kinoafisha.afisha.ui.StatusBarPadding
import org.example.kinoafisha.afisha.ui.components.GenreDrawer
import org.example.kinoafisha.afisha.ui.components.SearchBar
import org.example.kinoafisha.afisha.ui.screens.DetailsScreen
import org.example.kinoafisha.afisha.ui.screens.FavoritesScreen
import org.example.kinoafisha.afisha.ui.screens.FeedScreen
import org.example.kinoafisha.afisha.vm.FeedViewModel
import org.example.kinoafisha.core.domain.model.Movie
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private const val MenuBackAnimNanos = 320_000_000L
enum class AfishaScreen { Feed, Favorites }

data class DetailsNav(
    val movieId: Long,
    val preview: Movie?,
)

private val TopBarHeight = 64.dp
private val BottomNavHeight = 64.dp

@Composable
fun AfishaNav(
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
        val showBottomNav = useBottomNav && !showDetails
        val bottomChrome =
            NavBarPadding + if (showBottomNav) BottomNavHeight else 0.dp

        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KinoColors.Bg.copy(alpha = 0.92f))
                    .border(width = 0.dp, color = colors.line)
                    .height(TopBarHeight + StatusBarPadding)
                    .padding(top = StatusBarPadding)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MenuBackButton(
                    showAsBack = showDetails,
                    onClick = {
                        if (showDetails) {
                            details = null
                            screen = prevScreen
                        } else {
                            menuOpen = true
                        }
                    },
                )

                if (!useBottomNav) {
                    TopNavLink(
                        label = stringResource(Res.string.nav_new),
                        selected = !showDetails && screen == AfishaScreen.Feed,
                        onClick = { selectScreen(AfishaScreen.Feed) },
                    )
                    TopNavLink(
                        label = stringResource(Res.string.nav_favorites),
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
                    .padding(bottom = bottomChrome),
            ) {
                Box(Modifier.widthIn(max = 960.dp).fillMaxSize().align(Alignment.TopCenter)) {
                    when {
                        showDetails -> {
                            val nav = details!!
                            DetailsScreen(
                                movieId = nav.movieId,
                                preview = nav.preview,
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

        if (showBottomNav) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(colors.bgSoft.copy(alpha = 0.95f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BottomNavHeight),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BottomNavItem(
                        label = stringResource(Res.string.nav_new),
                        icon = Res.drawable.ic_clapperboard,
                        selected = screen == AfishaScreen.Feed,
                        onClick = { selectScreen(AfishaScreen.Feed) },
                        modifier = Modifier.weight(1f),
                    )
                    BottomNavItem(
                        label = stringResource(Res.string.nav_favorites),
                        icon = Res.drawable.ic_heart,
                        selected = screen == AfishaScreen.Favorites,
                        onClick = { selectScreen(AfishaScreen.Favorites) },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(NavBarPadding))
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
private fun MenuBackButton(
    showAsBack: Boolean,
    onClick: () -> Unit,
) {
    val colors = KinoTheme.colors
    val progress = remember { Animatable(if (showAsBack) 1f else 0f) }
    // Покадровая анимация через withFrameNanos — не зависит от Animator duration scale.
    LaunchedEffect(showAsBack) {
        val target = if (showAsBack) 1f else 0f
        val start = progress.value
        if (start == target) return@LaunchedEffect
        val startNanos = withFrameNanos { it }
        while (true) {
            val t = ((withFrameNanos { it } - startNanos).toFloat() / MenuBackAnimNanos)
                .coerceIn(0f, 1f)
            progress.snapTo(lerp(start, target, FastOutSlowInEasing.transform(t)))
            if (t >= 1f) break
        }
    }
    val description = stringResource(
        if (showAsBack) Res.string.action_back else Res.string.cd_menu,
    )
    val iconColor = KinoColors.Text
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, colors.line, RoundedCornerShape(10.dp))
            .background(colors.bgSoft)
            .semantics { contentDescription = description }
            .clickable(onClick = onClick)
            .drawBehind {
                // Рисуем в центре кнопки (иконка ~20dp)
                val iconSize = size.minDimension * 0.5f
                val origin = Offset(
                    (size.width - iconSize) / 2f,
                    (size.height - iconSize) / 2f,
                )
                drawMenuArrow(
                    progress = progress.value,
                    color = iconColor,
                    origin = origin,
                    iconSize = iconSize,
                )
            },
    )
}

private fun DrawScope.drawMenuArrow(
    progress: Float,
    color: Color,
    origin: Offset,
    iconSize: Float,
) {
    val stroke = iconSize * 0.1f
    val left = origin.x + iconSize * 0.12f
    val right = origin.x + iconSize * 0.88f
    val cy = origin.y + iconSize / 2f
    val gap = iconSize * 0.26f

    // 0 = три горизонтальные линии, 1 = стрелка ←
    val topStart = Offset(left, lerp(cy - gap, cy, progress))
    val topEnd = Offset(
        lerp(right, left + iconSize * 0.42f, progress),
        lerp(cy - gap, cy - iconSize * 0.28f, progress),
    )
    val midStart = Offset(lerp(left, left + iconSize * 0.1f, progress), cy)
    val midEnd = Offset(right, cy)
    val botStart = Offset(left, lerp(cy + gap, cy, progress))
    val botEnd = Offset(
        lerp(right, left + iconSize * 0.42f, progress),
        lerp(cy + gap, cy + iconSize * 0.28f, progress),
    )

    drawLine(color, topStart, topEnd, stroke, StrokeCap.Round)
    drawLine(color, midStart, midEnd, stroke, StrokeCap.Round)
    drawLine(color, botStart, botEnd, stroke, StrokeCap.Round)
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
