package org.example.kinoafisha.afisha.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.badge_new
import kinoafisha.feature.afisha.generated.resources.em_dash
import kinoafisha.feature.afisha.generated.resources.ic_film_strip
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoRadii
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.util.isNew
import org.example.kinoafisha.afisha.util.metaLine
import org.example.kinoafisha.afisha.util.posterUrl
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.Movie
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MovieCard(
    movie: Movie,
    isFavorite: Boolean,
    layout: FeedLayout,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    val shape = RoundedCornerShape(KinoRadii.Lg)
    val meta = movie.metaLine(stringResource(Res.string.em_dash))

    if (layout == FeedLayout.Grid) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, colors.line, shape)
                .background(colors.bgCard)
                .clickable(onClick = onClick)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Poster(movie = movie, modifier = Modifier.fillMaxWidth())
            Text(
                text = movie.title,
                color = KinoColors.Text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = meta,
                color = colors.textDim,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RatingPill(
                    rating = movie.rating,
                    voteCount = movie.voteCount,
                    showVotes = false,
                )
                Spacer(Modifier.weight(1f))
                FavButton(
                    isFavorite = isFavorite,
                    onClick = onToggleFavorite,
                    size = 34.dp,
                    iconSize = 16.dp,
                )
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, colors.line, shape)
                .background(colors.bgCard)
                .clickable(onClick = onClick)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Poster(
                movie = movie,
                modifier = Modifier
                    .width(92.dp)
                    .aspectRatio(2f / 3f),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    color = KinoColors.Text,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = meta,
                    color = colors.textDim,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp),
                )
                if (movie.overview.isNotBlank()) {
                    Text(
                        text = movie.overview,
                        color = colors.textDim,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RatingPill(
                        rating = movie.rating,
                        voteCount = movie.voteCount,
                        showVotes = true,
                    )
                    Spacer(Modifier.weight(1f))
                    FavButton(
                        isFavorite = isFavorite,
                        onClick = onToggleFavorite,
                    )
                }
            }
        }
    }
}

@Composable
private fun Poster(
    movie: Movie,
    modifier: Modifier = Modifier,
) {
    val colors = KinoTheme.colors
    val url = movie.posterUrl()
    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(KinoRadii.Md))
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
                modifier = Modifier.size(36.dp),
            )
        }
        if (movie.isNew()) {
            Text(
                text = stringResource(Res.string.badge_new),
                color = colors.accent2,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .background(KinoColors.Bg.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            )
        }
    }
}
