package org.example.kinoafisha.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.example.kinoafisha.core.database.dao.MovieDao
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.MovieDetails
import org.example.kinoafisha.core.domain.model.PagedMovies
import org.example.kinoafisha.core.domain.model.SortOrder
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.example.kinoafisha.core.network.MovieApi
import org.koin.core.annotation.Singleton

@Singleton
class MovieRepositoryImpl(
    @Suppress("unused") private val api: MovieApi,
    @Suppress("unused") private val movieDao: MovieDao,
) : MovieRepository {

    override suspend fun getFeed(
        query: String?,
        genreId: Int?,
        sort: SortOrder,
        page: Int,
    ): PagedMovies = throw NotImplementedError("")

    override suspend fun getGenres(): List<Genre> = throw NotImplementedError("")

    override suspend fun getMovieDetails(id: Long): MovieDetails =
        throw NotImplementedError("")

    override fun observeFavorites(): Flow<List<Movie>> = flowOf(emptyList())

    override suspend fun toggleFavorite(movie: Movie) = Unit

    override fun observeLayout(): Flow<FeedLayout> = flowOf(FeedLayout.List)

    override suspend fun setLayout(layout: FeedLayout) = Unit
}
