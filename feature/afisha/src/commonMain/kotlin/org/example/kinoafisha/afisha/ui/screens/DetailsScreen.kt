package org.example.kinoafisha.afisha.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.action_retry
import kinoafisha.feature.afisha.generated.resources.em_dash
import kinoafisha.feature.afisha.generated.resources.error_load
import kinoafisha.feature.afisha.generated.resources.error_reviews
import kinoafisha.feature.afisha.generated.resources.ic_film_strip
import kinoafisha.feature.afisha.generated.resources.ic_star
import kinoafisha.feature.afisha.generated.resources.movie_meta_film_year
import kinoafisha.feature.afisha.generated.resources.overview_missing
import kinoafisha.feature.afisha.generated.resources.reviews_count
import kinoafisha.feature.afisha.generated.resources.reviews_empty
import kinoafisha.feature.afisha.generated.resources.reviews_loading
import kinoafisha.feature.afisha.generated.resources.review_author_anonymous
import kinoafisha.feature.afisha.generated.resources.reviews_title
import kinoafisha.feature.afisha.generated.resources.votes_suffix
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.ui.atoms.FavButton
import org.example.kinoafisha.afisha.ui.atoms.RatingPill
import org.example.kinoafisha.afisha.util.posterUrl
import org.example.kinoafisha.afisha.vm.DetailsViewModel
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.Review
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.round

@Composable
fun DetailsScreen(
    movieId: Long,
    preview: Movie?,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val colors = KinoTheme.colors

    LaunchedEffect(movieId) {
        viewModel.open(movieId, preview)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        when {
            state.loading && state.movie == null -> {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.accent2)
                }
            }

            state.hasError && state.movie == null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(Res.string.error_load), color = colors.textDim)
                    TextButton(onClick = viewModel::retry) {
                        Text(stringResource(Res.string.action_retry), color = colors.accent2)
                    }
                }
            }

            state.movie != null -> {
                val movie = state.movie!!
                DetailsBody(
                    movie = movie,
                    certification = state.certification,
                    isFavorite = state.isFavorite,
                    onToggleFavorite = viewModel::toggleFav,
                )

                Spacer(Modifier.height(28.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.reviews_title),
                        color = KinoColors.Text,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    if (state.reviews.isNotEmpty()) {
                        Text(
                            text = pluralStringResource(
                                Res.plurals.reviews_count,
                                state.reviews.size,
                                state.reviews.size,
                            ),
                            color = colors.textDim,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                when {
                    state.reviewsLoading -> Text(
                        stringResource(Res.string.reviews_loading),
                        color = colors.textDim,
                    )
                    state.reviewsError -> Text(
                        stringResource(Res.string.error_reviews),
                        color = colors.textDim,
                    )
                    state.reviews.isEmpty() -> Text(
                        stringResource(Res.string.reviews_empty),
                        color = colors.textDim,
                    )
                    else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.reviews.forEach { review ->
                            ReviewCard(review)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsBody(
    movie: Movie,
    certification: String?,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    val colors = KinoTheme.colors
    BoxWithConstraints {
        val narrow = maxWidth < 720.dp
        if (narrow) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                PosterLarge(movie)
                InfoBlock(movie, certification, isFavorite, onToggleFavorite)
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(26.dp),
                verticalAlignment = Alignment.Top,
            ) {
                PosterLarge(movie, modifier = Modifier.width(220.dp))
                InfoBlock(
                    movie = movie,
                    certification = certification,
                    isFavorite = isFavorite,
                    onToggleFavorite = onToggleFavorite,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PosterLarge(
    movie: Movie,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    val url = movie.posterUrl(large = true)
    Box(
        modifier = modifier
            .widthIn(max = 240.dp)
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(KinoRadii.Lg))
            .background(colors.bgHover),
        contentAlignment = Alignment.Center,
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                painter = painterResource(Res.drawable.ic_film_strip),
                contentDescription = null,
                tint = colors.textDim,
                modifier = Modifier.size(72.dp),
            )
        }
    }
}

@Composable
private fun InfoBlock(
    movie: Movie,
    certification: String?,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    val year = movie.year?.toString() ?: stringResource(Res.string.em_dash)
    val genres = movie.genres.joinToString(", ") { it.name }
    val meta = buildString {
        append(stringResource(Res.string.movie_meta_film_year, year))
        if (genres.isNotEmpty()) append(" · $genres")
        if (!certification.isNullOrBlank()) append(" · $certification")
    }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = movie.title,
                color = KinoColors.Text,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
                modifier = Modifier.weight(1f),
            )
            FavButton(
                isFavorite = isFavorite,
                onClick = onToggleFavorite,
                size = 46.dp,
                iconSize = 22.dp,
            )
        }
        Text(
            text = meta,
            color = colors.textDim,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 10.dp),
        )
        RatingPill(
            rating = movie.rating,
            voteCount = movie.voteCount,
            showVotes = true,
            votesSuffix = stringResource(Res.string.votes_suffix),
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            text = movie.overview.ifBlank { stringResource(Res.string.overview_missing) },
            color = colors.textDim,
            modifier = Modifier
                .padding(top = 16.dp)
                .widthIn(max = 560.dp),
        )
    }
}

@Composable
private fun ReviewCard(review: Review) {
    val colors = KinoTheme.colors
    val shape = RoundedCornerShape(KinoRadii.Lg)
    val author = review.author.ifBlank { stringResource(Res.string.review_author_anonymous) }
    val hue = author.fold(0) { acc, c -> acc + c.code } % 360
    val avatarColor = Color.hsl(hue.toFloat(), 0.55f, 0.45f)
    val date = review.createdAt
        ?.take(10)
        ?.split("-")
        ?.reversed()
        ?.joinToString(".")
        .orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, colors.line, shape)
            .background(colors.bgCard)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = author.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = KinoColors.OnAccent,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(author, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = KinoColors.Text)
                if (date.isNotEmpty()) {
                    Text(date, color = colors.textDim, fontSize = 12.sp)
                }
            }
            review.rating?.let { rating ->
                val stars = round(rating / 2).toInt().coerceIn(0, 5)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { index ->
                        Icon(
                            painter = painterResource(Res.drawable.ic_star),
                            contentDescription = null,
                            tint = if (index < stars) colors.gold else colors.line,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                }
            }
        }
        Text(
            text = review.content,
            color = colors.textDim,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}
