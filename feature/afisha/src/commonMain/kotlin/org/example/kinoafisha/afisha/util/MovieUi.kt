package org.example.kinoafisha.afisha.util

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kinoafisha.feature.afisha.generated.resources.Res
import kinoafisha.feature.afisha.generated.resources.ic_arrow_down
import kinoafisha.feature.afisha.generated.resources.ic_arrow_up
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.SortOrder
import org.jetbrains.compose.resources.DrawableResource

private const val IMG_CARD = "https://image.tmdb.org/t/p/w342"
private const val IMG_DETAILS = "https://image.tmdb.org/t/p/w500"
private const val NEW_WINDOW_MS = 21L * 86_400_000L

fun Movie.posterUrl(large: Boolean = false): String? {
    val path = posterPath ?: return null
    if (path.startsWith("http")) return path
    return (if (large) IMG_DETAILS else IMG_CARD) + path
}

@OptIn(ExperimentalTime::class)
fun Movie.isNew(
    nowMillis: Long = Clock.System.now().toEpochMilliseconds(),
): Boolean {
    val date = releaseDate ?: return false
    if (!date.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) return false
    return try {
        val released = Instant.parse("${date}T00:00:00Z").toEpochMilliseconds()
        nowMillis - released in 0 until NEW_WINDOW_MS
    } catch (_: Exception) {
        false
    }
}

fun Movie.metaLine(): String {
    val year = year?.toString() ?: "—"
    val genrePart = genres.take(3).joinToString(", ") { it.name }
    return if (genrePart.isNotEmpty()) "$year · $genrePart" else year
}

fun sortMovies(list: List<Movie>, sort: SortOrder): List<Movie> =
    when (sort) {
        SortOrder.RatingDesc ->
            list.sortedWith(
                compareByDescending<Movie> { it.rating }
                    .thenByDescending { it.voteCount },
            )
        SortOrder.RatingAsc ->
            list.sortedWith(
                compareBy<Movie> { it.rating }
                    .thenBy { it.voteCount },
            )
        SortOrder.YearDesc ->
            list.sortedByDescending { it.releaseDate.orEmpty() }
        SortOrder.YearAsc ->
            list.sortedBy { it.releaseDate.orEmpty() }
        SortOrder.Popularity -> list
    }

fun SortOrder.label(forFavorites: Boolean = false): String =
    when (this) {
        SortOrder.Popularity -> if (forFavorites) "По умолчанию" else "По популярности"
        SortOrder.RatingDesc, SortOrder.RatingAsc -> "По рейтингу"
        SortOrder.YearDesc, SortOrder.YearAsc -> "По году"
    }

fun SortOrder.directionIcon(): DrawableResource? =
    when (this) {
        SortOrder.RatingDesc, SortOrder.YearDesc -> Res.drawable.ic_arrow_down
        SortOrder.RatingAsc, SortOrder.YearAsc -> Res.drawable.ic_arrow_up
        SortOrder.Popularity -> null
    }

fun formatVotes(votes: Int): String {
    val s = votes.toString()
    val sb = StringBuilder()
    var i = s.length
    while (i > 0) {
        val start = (i - 3).coerceAtLeast(0)
        if (sb.isNotEmpty()) sb.insert(0, '\u00A0')
        sb.insert(0, s.substring(start, i))
        i = start
    }
    return sb.toString()
}

fun pluralRu(n: Int, one: String, few: String, many: String): String {
    val m10 = n % 10
    val m100 = n % 100
    return when {
        m10 == 1 && m100 != 11 -> one
        m10 in 2..4 && m100 !in 12..14 -> few
        else -> many
    }
}
