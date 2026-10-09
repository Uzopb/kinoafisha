package org.example.kinoafisha.afisha.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.util.formatVotes
import kotlin.math.round

@Composable
fun RatingPill(
    rating: Double,
    voteCount: Int,
    modifier: Modifier = Modifier,
    showVotes: Boolean = true,
    votesSuffix: String? = null,
) {
    val colors = KinoTheme.colors
    val tone = ratingTone(rating, colors.green, colors.gold, colors.ratingLow)
    Row(
        modifier = modifier
            .background(colors.bgHover, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = "★ ${formatRating(rating)}",
            color = tone,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        if (showVotes) {
            val votes = formatVotes(voteCount)
            Text(
                text = if (votesSuffix != null) "· $votes $votesSuffix" else "· $votes",
                color = colors.textDim,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

private fun formatRating(rating: Double): String {
    val tenths = round(rating * 10) / 10.0
    val whole = tenths.toInt()
    val frac = ((tenths * 10).toInt() % 10)
    return "$whole.$frac"
}

private fun ratingTone(
    rating: Double,
    high: Color,
    mid: Color,
    low: Color,
): Color = when {
    rating >= 7.5 -> high
    rating >= 6.0 -> mid
    else -> low
}
