package org.example.kinoafisha.core.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.kinoafisha.core.domain.model.FeedKey
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.FeedSnapshot
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.MovieDetails

interface MovieRepository {
    /** Локальный срез (cache ← Room). UI подписан только сюда. */
    fun observeFeed(key: FeedKey): Flow<FeedSnapshot>

    /** Фоновый sync: Remote → Room → cache. */
    suspend fun syncFeed(key: FeedKey, page: Int = 1, append: Boolean = false)

    fun observeGenres(): Flow<List<Genre>>

    suspend fun syncGenres()

    suspend fun getMovieDetails(id: Long): MovieDetails

    fun observeFavorites(): Flow<List<Movie>>

    suspend fun toggleFavorite(movie: Movie)

    fun observeLayout(): Flow<FeedLayout>

    suspend fun setLayout(layout: FeedLayout)
}
