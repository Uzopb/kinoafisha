package org.example.kinoafisha.core.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.kinoafisha.core.data.mapper.sortMovies
import org.example.kinoafisha.core.data.mapper.toDetails
import org.example.kinoafisha.core.data.mapper.toDomain
import org.example.kinoafisha.core.data.mapper.toEntity
import org.example.kinoafisha.core.data.mapper.toFavoriteEntity
import org.example.kinoafisha.core.data.mapper.toFeedLayout
import org.example.kinoafisha.core.data.mapper.toStorage
import org.example.kinoafisha.core.data.mapper.toTmdbSortBy
import org.example.kinoafisha.core.data.mapper.withPreservedDetailsFrom
import org.example.kinoafisha.core.data.store.LocalStore
import org.example.kinoafisha.core.database.dao.AppSettingsDao
import org.example.kinoafisha.core.database.dao.FavoriteDao
import org.example.kinoafisha.core.database.dao.FeedSliceDao
import org.example.kinoafisha.core.database.dao.GenreDao
import org.example.kinoafisha.core.database.dao.MovieDao
import org.example.kinoafisha.core.database.dao.PosterDao
import org.example.kinoafisha.core.database.entity.AppSettingsEntity
import org.example.kinoafisha.core.database.entity.FeedSliceEntity
import org.example.kinoafisha.core.database.entity.PosterEntity
import org.example.kinoafisha.core.domain.model.FeedKey
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.FeedSnapshot
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.MovieDetails
import org.example.kinoafisha.core.domain.model.SearchMode
import org.example.kinoafisha.core.domain.model.SortOrder
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.example.kinoafisha.core.network.MovieApi
import org.example.kinoafisha.core.network.dto.MovieDto
import org.example.kinoafisha.core.network.dto.PagedResponse
import org.example.kinoafisha.core.network.dto.PersonDto
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton

/**
 * Offline-first:
 * UI ← LocalStore ← Room ← (фон) Remote.
 * Сеть никогда не кормит Compose напрямую.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class MovieRepositoryImpl(
    private val api: MovieApi,
    private val movieDao: MovieDao,
    private val favoriteDao: FavoriteDao,
    private val genreDao: GenreDao,
    private val posterDao: PosterDao,
    private val feedSliceDao: FeedSliceDao,
    private val appSettingsDao: AppSettingsDao,
    private val store: LocalStore,
    @Named("images") private val imageClient: HttpClient,
) : MovieRepository {

    private val hydrateMutex = Mutex()
    private var hydrated = false

    @Volatile
    private var genreById: Map<Int, Genre> = emptyMap()

    override fun observeFeed(key: FeedKey): Flow<FeedSnapshot> =
        store.observeFeed(key).onStart { ensureHydrated() }

    override suspend fun syncFeed(key: FeedKey, page: Int, append: Boolean) {
        ensureHydrated()
        val feedKey = key.normalized()
        store.setRefreshing(feedKey, true)
        try {
            ensureGenres(forceNetwork = false)
            val response = fetchFeedPage(
                query = feedKey.query.takeIf { it.isNotEmpty() },
                searchMode = feedKey.searchMode,
                genreId = feedKey.genreId,
                sort = feedKey.sort,
                page = page,
            )
            persistNetworkPage(feedKey, response, append)
        } catch (_: Exception) {
            val hadLocal = ensureSliceInStore(feedKey)
            store.setRefreshing(feedKey, false)
            if (!hadLocal && store.sliceOrNull(feedKey)?.movieIds.isNullOrEmpty()) {
                throw IllegalStateException("No local feed for ${feedKey.storageKey()}")
            }
        }
    }

    override fun observeGenres(): Flow<List<Genre>> =
        store.observeGenres().onStart { ensureHydrated() }

    override suspend fun syncGenres() {
        ensureHydrated()
        ensureGenres(forceNetwork = true)
    }

    override suspend fun getMovieDetails(id: Long): MovieDetails {
        ensureHydrated()
        ensureGenres(forceNetwork = false)
        return try {
            val dto = api.movieDetails(id)
            movieDao.upsert(dto.toEntity())
            val posters = ensurePosters(listOf(dto.id to dto.posterPath))
            val details = dto.toDomain(posters[dto.id])
            store.publishMovies(listOf(details.movie))
            details
        } catch (e: Exception) {
            cachedDetails(id) ?: throw e
        }
    }

    override fun observeFavorites(): Flow<List<Movie>> =
        favoriteDao.observeAll()
            .onStart { ensureHydrated() }
            .mapLatest { list ->
                val posters = posterBytesByIds(list.map { it.id })
                val movies = list.map { it.toDomain(genreById, posters[it.id]) }
                store.publishMovies(movies)
                movies
            }

    override suspend fun toggleFavorite(movie: Movie) {
        if (favoriteDao.isFavorite(movie.id).first()) {
            favoriteDao.delete(movie.id)
        } else {
            favoriteDao.upsert(movie.toFavoriteEntity(addedAt = currentEpochMillis()))
            val path = movie.posterPath
            if (path != null) {
                val existing = posterDao.getById(movie.id)
                if (existing == null || existing.path != path) {
                    val bytes = movie.posterBytes ?: downloadPoster(path)
                    if (bytes != null) {
                        posterDao.upsert(PosterEntity(movie.id, path, bytes))
                    }
                }
            }
            store.publishMovies(listOf(movie))
        }
    }

    override fun observeLayout(): Flow<FeedLayout> =
        appSettingsDao.observe().map { it?.layout.toFeedLayout() }

    override suspend fun setLayout(layout: FeedLayout) {
        appSettingsDao.upsert(
            AppSettingsEntity(layout = layout.toStorage()),
        )
    }

    private suspend fun persistNetworkPage(
        key: FeedKey,
        response: PagedResponse<MovieDto>,
        append: Boolean,
    ) {
        upsertFeedMovies(response.results)
        val posters = ensurePosters(response.results.map { it.id to it.posterPath })
        val entities = movieDao.getByIds(response.results.map { it.id }).associateBy { it.id }
        var items = response.results.map { dto ->
            entities[dto.id]?.toDomain(genreById, posters[dto.id])
                ?: dto.toDomain(genreById, posters[dto.id])
        }
        val q = key.query
        items = if (q.isNotEmpty() || key.genreId == null) {
            sortMovies(items, key.sort)
        } else {
            items
        }

        store.publishMovies(items)

        val newIds = items.map { it.id }
        val prev = if (append) feedSliceDao.getByKey(key.storageKey()) else null
        val mergedIds = if (prev != null && append) {
            val seen = parseIds(prev.movieIds).toHashSet()
            parseIds(prev.movieIds) + newIds.filter { it !in seen }
        } else {
            newIds
        }
        val page = response.page
        val totalPages = response.totalPages.coerceAtLeast(1)
        val syncedAt = currentEpochMillis()

        feedSliceDao.upsert(
            FeedSliceEntity(
                key = key.storageKey(),
                movieIds = mergedIds.joinToString(","),
                page = page,
                totalPages = totalPages,
                syncedAt = syncedAt,
            ),
        )
        store.publishSlice(
            key = key,
            movieIds = mergedIds,
            page = page,
            totalPages = totalPages,
            syncedAt = syncedAt,
            fromCache = false,
            append = false, // уже смержили ids выше
        )
    }

    /** Поднимает срез из Room в store. true если данные есть. */
    private suspend fun ensureSliceInStore(key: FeedKey): Boolean {
        store.sliceOrNull(key)?.movieIds?.takeIf { it.isNotEmpty() }?.let { return true }
        val entity = feedSliceDao.getByKey(key.storageKey()) ?: return false
        val ids = parseIds(entity.movieIds)
        if (ids.isEmpty()) return false
        loadMoviesIntoStore(ids)
        store.publishSlice(
            key = key,
            movieIds = ids,
            page = entity.page,
            totalPages = entity.totalPages,
            syncedAt = entity.syncedAt,
            fromCache = true,
            append = false,
        )
        return true
    }

    private suspend fun loadMoviesIntoStore(ids: List<Long>) {
        if (ids.isEmpty()) return
        val entities = movieDao.getByIds(ids)
        val posters = posterBytesByIds(ids)
        store.publishMovies(entities.map { it.toDomain(genreById, posters[it.id]) })
    }

    private suspend fun ensureHydrated() {
        if (hydrated) return
        hydrateMutex.withLock {
            if (hydrated) return
            hydrateFromRoom()
            hydrated = true
        }
    }

    private suspend fun hydrateFromRoom() {
        val localGenres = runCatching { genreDao.getAll() }.getOrDefault(emptyList())
        if (localGenres.isNotEmpty()) {
            genreById = localGenres.associate { it.id to it.toDomain() }
            store.publishGenres(genreById.values.toList())
        }

        val slices = runCatching { feedSliceDao.getAll() }.getOrDefault(emptyList())
        val allIds = slices.flatMap { parseIds(it.movieIds) }.distinct()
        if (allIds.isNotEmpty()) {
            loadMoviesIntoStore(allIds)
        }
        for (slice in slices) {
            val ids = parseIds(slice.movieIds)
            // storageKey уже в entity.key — восстанавливаем только ids в store по ключу-строке
            store.publishSlice(
                key = feedKeyFromStorage(slice.key) ?: continue,
                movieIds = ids,
                page = slice.page,
                totalPages = slice.totalPages,
                syncedAt = slice.syncedAt,
                fromCache = true,
                append = false,
            )
        }
    }

    private fun feedKeyFromStorage(storageKey: String): FeedKey? {
        val parts = mutableMapOf<String, String>()
        for (part in storageKey.split('|')) {
            val idx = part.indexOf('=')
            if (idx <= 0) return null
            parts[part.substring(0, idx)] = part.substring(idx + 1)
        }
        val sort = runCatching {
            SortOrder.valueOf(parts["s"] ?: return null)
        }.getOrNull() ?: return null
        val g = parts["g"].orEmpty()
        val mode = parts["m"]
            ?.takeIf { it.isNotEmpty() }
            ?.let { raw -> runCatching { SearchMode.valueOf(raw) }.getOrNull() }
            ?: SearchMode.Title
        return FeedKey(
            query = parts["q"].orEmpty(),
            searchMode = mode,
            genreId = g.takeIf { it.isNotEmpty() }?.toIntOrNull(),
            sort = sort,
        )
    }

    private suspend fun fetchFeedPage(
        query: String?,
        searchMode: SearchMode,
        genreId: Int?,
        sort: SortOrder,
        page: Int,
    ): PagedResponse<MovieDto> {
        val q = query?.trim().orEmpty()
        if (q.isNotEmpty()) {
            return when (searchMode) {
                SearchMode.Title -> api.search(query = q, page = page)
                SearchMode.Director, SearchMode.Actor ->
                    searchByPerson(query = q, mode = searchMode, sort = sort, page = page)
            }
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

    private suspend fun searchByPerson(
        query: String,
        mode: SearchMode,
        sort: SortOrder,
        page: Int,
    ): PagedResponse<MovieDto> {
        val people = api.searchPerson(query = query, page = 1).results
        val person = pickPerson(people, mode)
            ?: return PagedResponse(page = page, results = emptyList(), totalPages = 0)
        val personId = person.id.toString()
        return api.discover(
            sortBy = sort.toTmdbSortBy(),
            voteCountGte = voteCountFloor(sort),
            withCast = personId.takeIf { mode == SearchMode.Actor },
            withCrew = personId.takeIf { mode == SearchMode.Director },
            page = page,
        )
    }

    private fun pickPerson(people: List<PersonDto>, mode: SearchMode): PersonDto? {
        val preferredDept = when (mode) {
            SearchMode.Director -> "Directing"
            SearchMode.Actor -> "Acting"
            SearchMode.Title -> return null
        }
        return people.firstOrNull { it.knownForDepartment.equals(preferredDept, ignoreCase = true) }
            ?: people.firstOrNull()
    }

    private suspend fun upsertFeedMovies(dtos: List<MovieDto>) {
        if (dtos.isEmpty()) return
        val existing = movieDao.getByIds(dtos.map { it.id }).associateBy { it.id }
        movieDao.upsertAll(
            dtos.map { dto ->
                dto.toEntity().withPreservedDetailsFrom(existing[dto.id])
            },
        )
    }

    private suspend fun cachedDetails(id: Long): MovieDetails? {
        val bytes = posterDao.getById(id)?.bytes
        movieDao.getById(id)?.let {
            val details = it.toDetails(genreById, bytes)
            store.publishMovies(listOf(details.movie))
            return details
        }
        favoriteDao.getById(id)?.let {
            val details = it.toDetails(genreById, bytes)
            store.publishMovies(listOf(details.movie))
            return details
        }
        return null
    }

    private suspend fun ensurePosters(idsAndPaths: List<Pair<Long, String?>>): Map<Long, ByteArray> {
        val wanted = idsAndPaths.mapNotNull { (id, path) -> path?.let { id to it } }
        if (wanted.isEmpty()) return emptyMap()
        val existing = posterDao.getByIds(wanted.map { it.first }).associateBy { it.movieId }
        val result = HashMap<Long, ByteArray>(wanted.size)
        val toFetch = mutableListOf<Pair<Long, String>>()

        for ((id, path) in wanted) {
            val cached = existing[id]
            if (cached != null && cached.path == path) {
                result[id] = cached.bytes
            } else {
                toFetch += id to path
            }
        }

        if (toFetch.isNotEmpty()) {
            val downloaded = coroutineScope {
                toFetch.map { (id, path) ->
                    async {
                        val bytes = downloadPoster(path) ?: return@async null
                        PosterEntity(id, path, bytes)
                    }
                }.awaitAll().filterNotNull()
            }
            if (downloaded.isNotEmpty()) {
                posterDao.upsertAll(downloaded)
                downloaded.forEach { result[it.movieId] = it.bytes }
            }
        }
        return result
    }

    private suspend fun posterBytesByIds(ids: List<Long>): Map<Long, ByteArray> {
        if (ids.isEmpty()) return emptyMap()
        return posterDao.getByIds(ids).associate { it.movieId to it.bytes }
    }

    private suspend fun downloadPoster(path: String): ByteArray? =
        runCatching {
            imageClient.get("$IMG_BASE$path").readRawBytes()
        }.getOrNull()

    private suspend fun ensureGenres(forceNetwork: Boolean) {
        if (!forceNetwork && genreById.isNotEmpty()) return
        try {
            val remote = api.genres().genres
            genreById = remote.associate { dto -> dto.id to dto.toDomain() }
            runCatching { genreDao.upsertAll(remote.map { it.toEntity() }) }
            store.publishGenres(genreById.values.toList())
        } catch (_: Exception) {
            if (genreById.isNotEmpty()) {
                store.publishGenres(genreById.values.toList())
                return
            }
            val local = runCatching { genreDao.getAll() }.getOrDefault(emptyList())
            if (local.isEmpty()) return
            genreById = local.associate { it.id to it.toDomain() }
            store.publishGenres(genreById.values.toList())
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

    private fun parseIds(raw: String): List<Long> =
        raw.split(',')
            .mapNotNull { it.trim().toLongOrNull() }

    private companion object {
        const val IMG_BASE = "https://image.tmdb.org/t/p/w342"
    }
}
