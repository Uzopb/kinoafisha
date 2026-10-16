package org.example.kinoafisha.core.data.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import org.example.kinoafisha.core.domain.model.FeedKey
import org.example.kinoafisha.core.domain.model.FeedSnapshot
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.koin.core.annotation.Singleton

/**
 * Горячий кэш: catalog (id→Movie) + slices (FeedKey→ids) + genres.
 * UI читает только отсюда; Room наполняет через publish* после durable write.
 */
@Singleton
class LocalStore {

    data class SliceState(
        val movieIds: List<Long> = emptyList(),
        val page: Int = 1,
        val totalPages: Int = 1,
        val syncedAt: Long = 0L,
        val refreshing: Boolean = false,
        val fromCache: Boolean = true,
    )

    private val catalog = MutableStateFlow<Map<Long, Movie>>(emptyMap())
    private val slices = MutableStateFlow<Map<String, SliceState>>(emptyMap())
    private val genres = MutableStateFlow<List<Genre>>(emptyList())

    fun observeGenres(): Flow<List<Genre>> = genres

    fun genresSnapshot(): List<Genre> = genres.value

    fun observeFeed(key: FeedKey): Flow<FeedSnapshot> {
        val storageKey = key.normalized().storageKey()
        return combine(catalog, slices) { movies, sliceMap ->
            val slice = sliceMap[storageKey] ?: SliceState(refreshing = false)
            val items = slice.movieIds.mapNotNull { movies[it] }
            FeedSnapshot(
                key = key.normalized(),
                items = items,
                page = slice.page,
                totalPages = slice.totalPages,
                refreshing = slice.refreshing,
                fromCache = slice.fromCache,
            )
        }
    }

    fun publishMovies(movies: List<Movie>) {
        if (movies.isEmpty()) return
        catalog.update { current ->
            current + movies.associate { movie ->
                val prev = current[movie.id]
                movie.id to mergeMovie(prev, movie)
            }
        }
    }

    /** Лента без credits не затирает уже загруженные режиссёра/актёров. */
    private fun mergeMovie(prev: Movie?, next: Movie): Movie {
        if (prev == null) return next
        return next.copy(
            director = next.director ?: prev.director,
            cast = next.cast.ifEmpty { prev.cast },
            posterBytes = next.posterBytes ?: prev.posterBytes,
        )
    }

    fun publishGenres(list: List<Genre>) {
        genres.value = list
    }

    fun setRefreshing(key: FeedKey, refreshing: Boolean) {
        val storageKey = key.normalized().storageKey()
        slices.update { map ->
            val prev = map[storageKey] ?: SliceState()
            map + (storageKey to prev.copy(refreshing = refreshing))
        }
    }

    fun publishSlice(
        key: FeedKey,
        movieIds: List<Long>,
        page: Int,
        totalPages: Int,
        syncedAt: Long,
        fromCache: Boolean,
        append: Boolean,
    ) {
        val storageKey = key.normalized().storageKey()
        slices.update { map ->
            val prev = map[storageKey]
            val ids = if (append && prev != null) {
                val seen = prev.movieIds.toHashSet()
                prev.movieIds + movieIds.filter { it !in seen }
            } else {
                movieIds
            }
            map + (
                storageKey to SliceState(
                    movieIds = ids,
                    page = page,
                    totalPages = totalPages,
                    syncedAt = syncedAt,
                    refreshing = false,
                    fromCache = fromCache,
                )
                )
        }
    }

    fun sliceOrNull(key: FeedKey): SliceState? =
        slices.value[key.normalized().storageKey()]
}
