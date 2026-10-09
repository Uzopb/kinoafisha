package org.example.kinoafisha.afisha.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.ic_ticket
import org.example.kinoafisha.afisha.theme.KinoColors
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.core.domain.model.Genre
import org.jetbrains.compose.resources.painterResource

@Composable
fun GenreDrawer(
    open: Boolean,
    genres: List<Genre>,
    selectedGenreId: Int?,
    onSelect: (Int?) -> Unit,
    onClose: () -> Unit,
) {
    val colors = KinoTheme.colors
    AnimatedVisibility(
        visible = open,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(KinoColors.Bg.copy(alpha = 0.6f))
                    .clickable(onClick = onClose),
            )
            AnimatedVisibility(
                visible = open,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it },
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 300.dp)
                        .fillMaxWidth(0.85f)
                        .background(colors.bgSoft)
                        .padding(bottom = 16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_ticket),
                            contentDescription = null,
                            tint = colors.accent2,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(20.dp),
                        )
                        Text(
                            text = "Жанры",
                            color = KinoColors.Text,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "✕",
                            color = colors.textDim,
                            modifier = Modifier
                                .clickable(onClick = onClose)
                                .padding(8.dp),
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 10.dp),
                    ) {
                        genres.forEach { genre ->
                            val active = genre.id == selectedGenreId
                            val bg = if (active) {
                                Brush.linearGradient(listOf(colors.accent, colors.accent2))
                            } else {
                                Brush.linearGradient(listOf(colors.bgSoft, colors.bgSoft))
                            }
                            Text(
                                text = genre.name.replaceFirstChar { it.uppercase() },
                                color = if (active) KinoColors.OnAccent else colors.textDim,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bg)
                                    .clickable {
                                        onSelect(genre.id)
                                        onClose()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
