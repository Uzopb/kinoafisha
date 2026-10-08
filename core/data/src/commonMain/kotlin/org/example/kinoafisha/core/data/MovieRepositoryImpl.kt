package org.example.kinoafisha.core.data

import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.example.kinoafisha.core.data.mapper.sortMovies
import org.example.kinoafisha.core.data.mapper.toDomain
import org.example.kinoafisha.core.data.mapper.toEntity
import org.example.kinoafisha.core.data.mapper.toFavoriteEntity
import org.example.kinoafisha.core.data.mapper.toFeedLayout
import org.example.kinoafisha.core.data.mapper.toStorage
import org.example.kinoafisha.core.data.mapper.toTmdbSortBy
import org.example.kinoafisha.core.database.dao.AppSettingsDao
import org.example.kinoafisha.core.database.dao.FavoriteDao
import org.example.kinoafisha.core.database.dao.MovieDao
import org.example.kinoafisha.core.database.entity.AppSettingsEntity
import org.example.kinoafisha.core.database.entity.MovieEntity
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.MovieDetails
import org.example.kinoafisha.core.domain.model.PagedMovies
import org.example.kinoafisha.core.domain.model.SortOrder
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.example.kinoafisha.core.network.MovieApi
import org.example.kinoafisha.core.network.dto.MovieDto
import org.example.kinoafisha.core.network.dto.PagedResponse
import org.koin.core.annotation.Singleton

@Singleton
class MovieRepositoryImpl(
    private val api: MovieApi,
    private val movieDao: MovieDao,
    private val favoriteDao: FavoriteDao,
    private val appSettingsDao: AppSettingsDao,
) : MovieRepository {

    @Volatile
    private var genreById: Map<Int, Genre> = emptyMap()

    override suspend fun getFeed(
        query: String?,
        genreId: Int?,
        sort: SortOrder,
        page: Int,
    ): PagedMovies {
        ensureGenres()
        return try {
            val response = fetchFeedPage(query, genreId, sort, page)
            movieDao.upsertAll(response.results.map(MovieDto::toEntity))
            val items = response.results.map { it.toDomain(genreById) }
            val q = query?.trim().orEmpty()
            val sorted =
                if (q.isNotEmpty() || genreId == null) sortMovies(items, sort) else items
            PagedMovies(
                items = sorted,
                page = response.page,
                totalPages = response.totalPages.coerceAtLeast(1),
            )
        } catch (e: Exception) {
            val cached = movieDao.getAll()
            if (cached.isEmpty()) throw e
            PagedMovies(
                items = cached.map(MovieEntity::toDomain),
                page = 1,
                totalPages = 1,
            )
        }
    }

    override suspend fun getGenres(): List<Genre> {
        ensureGenres()
        return genreById.values.toList()
    }

    override suspend fun getMovieDetails(id: Long): MovieDetails =
        api.movieDetails(id).toDomain()

    override fun observeFavorites(): Flow<List<Movie>> =
        favoriteDao.observeAll().map { list ->
            list.map { it.toDomain(genreById) }
        }

    override suspend fun toggleFavorite(movie: Movie) {
        if (favoriteDao.isFavorite(movie.id).first()) {
            favoriteDao.delete(movie.id)
        } else {
            favoriteDao.upsert(movie.toFavoriteEntity(addedAt = currentEpochMillis()))
        }
    }

    override fun observeLayout(): Flow<FeedLayout> =
        appSettingsDao.observe().map { it?.layout.toFeedLayout() }

    override suspend fun setLayout(layout: FeedLayout) {
        appSettingsDao.upsert(
            AppSettingsEntity(layout = layout.toStorage()),
        )
    }

    /**
     * Ветки как fetchFeedPage в design/app.js:
     * search → genre+discover → sort+окно дат → now_playing.
     */
    private suspend fun fetchFeedPage(
        query: String?,
        genreId: Int?,
        sort: SortOrder,
        page: Int,
    ): PagedResponse<MovieDto> {
        val q = query?.trim().orEmpty()
        if (q.isNotEmpty()) {
            return api.search(query = q, page = page)
        }
        if (genreId != null) {
            return api.discover(
                genreId = genreId,
                sortBy = sort.toTmdbSortBy(),
                voteCountGte = voteCountFloor(sort),
                page = page,
            )
        }
        if (sort != SortOrder.Popularity) {
            val (from, to) = releaseWindow()
            return api.discover(
                sortBy = sort.toTmdbSortBy(),
                dateFrom = from,
                dateTo = to,
                voteCountGte = voteCountFloor(sort),
                page = page,
            )
        }
        return api.nowPlaying(page = page)
    }

    private suspend fun ensureGenres() {
        if (genreById.isNotEmpty()) return
        genreById = api.genres().genres.associate { dto ->
            dto.id to dto.toDomain()
        }
    }

    private fun voteCountFloor(sort: SortOrder): Int? =
        when (sort) {
            SortOrder.RatingDesc, SortOrder.RatingAsc -> 50
            else -> null
        }

    @OptIn(ExperimentalTime::class)
    private fun releaseWindow(): Pair<String, String> {
        val now = Clock.System.now()
        val from = now - 62.days
        return from.toString().take(10) to now.toString().take(10)
    }

    @OptIn(ExperimentalTime::class)
    private fun currentEpochMillis(): Long =
        Clock.System.now().toEpochMilliseconds()
}
