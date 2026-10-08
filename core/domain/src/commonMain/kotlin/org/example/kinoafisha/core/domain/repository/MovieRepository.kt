package org.example.kinoafisha.core.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.MovieDetails
import org.example.kinoafisha.core.domain.model.PagedMovies
import org.example.kinoafisha.core.domain.model.SortOrder

interface MovieRepository {
    suspend fun getFeed(
        query: String? = null,
        genreId: Int? = null,
        sort: SortOrder = SortOrder.Popularity,
        page: Int = 1,
    ): PagedMovies

    suspend fun getGenres(): List<Genre>

    suspend fun getMovieDetails(id: Long): MovieDetails

    fun observeFavorites(): Flow<List<Movie>>

    suspend fun toggleFavorite(movie: Movie)

    fun observeLayout(): Flow<FeedLayout>

    suspend fun setLayout(layout: FeedLayout)
}
