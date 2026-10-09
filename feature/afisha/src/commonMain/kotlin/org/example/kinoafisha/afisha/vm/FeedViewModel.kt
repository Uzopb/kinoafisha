package org.example.kinoafisha.afisha.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kinoafisha.afisha.util.sortMovies
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.SortOrder
import org.example.kinoafisha.core.domain.usecase.GetFeedUseCase
import org.example.kinoafisha.core.domain.usecase.GetGenresUseCase
import org.example.kinoafisha.core.domain.usecase.ObserveFavoritesUseCase
import org.example.kinoafisha.core.domain.usecase.ObserveLayoutUseCase
import org.example.kinoafisha.core.domain.usecase.SetLayoutUseCase
import org.example.kinoafisha.core.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.annotation.KoinViewModel

/** Mirrors design/app.js feed-related `state`. */
data class FeedState(
    val query: String = "",
    val genreId: Int? = null,
    val genres: List<Genre> = emptyList(),
    val sort: SortOrder = SortOrder.Popularity,
    val page: Int = 1,
    val totalPages: Int = 1,
    val layout: FeedLayout = FeedLayout.List,
    val items: List<Movie> = emptyList(),
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val loadMoreError: Boolean = false,
    val error: String? = null,
    val favoriteIds: Set<Long> = emptySet(),
) {
    val hasMore: Boolean get() = page < totalPages && items.isNotEmpty()
    val title: String
        get() = genres.find { it.id == genreId }?.name?.replaceFirstChar { it.uppercase() }
            ?: "Новинки"
}

@KoinViewModel
class FeedViewModel(
    private val getFeed: GetFeedUseCase,
    private val getGenres: GetGenresUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val observeLayout: ObserveLayoutUseCase,
    private val setLayoutUseCase: SetLayoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState(loading = true))
    val state: StateFlow<FeedState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var loadJob: Job? = null

    init {
        observeFavorites()
            .onEach { favs ->
                _state.update { it.copy(favoriteIds = favs.map { m -> m.id }.toSet()) }
            }
            .launchIn(viewModelScope)

        observeLayout()
            .onEach { layout -> _state.update { it.copy(layout = layout) } }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            runCatching { getGenres() }
                .onSuccess { list -> _state.update { it.copy(genres = list) } }
            refresh()
        }
    }

    /** Debounced search; clears genre (mutually exclusive, as in JS). */
    fun onQueryChange(query: String) {
        _state.update {
            it.copy(
                query = query,
                genreId = if (query.isNotBlank()) null else it.genreId,
            )
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            refresh()
        }
    }

    fun clearQuery() {
        searchJob?.cancel()
        _state.update { it.copy(query = "") }
        refresh()
    }

    fun setSort(sort: SortOrder) {
        if (_state.value.sort == sort) return
        _state.update { it.copy(sort = sort) }
        refresh()
    }

    fun selectGenre(genreId: Int?) {
        searchJob?.cancel()
        val current = _state.value.genreId
        val next = if (genreId != null && genreId == current) null else genreId
        _state.update { it.copy(genreId = next, query = "") }
        refresh()
    }

    /** Reset genre/query when tapping «Новинки» tab again (design/app.js). */
    fun resetFiltersAndRefresh() {
        searchJob?.cancel()
        _state.update { it.copy(genreId = null, query = "") }
        refresh()
    }

    fun loadMore() {
        val s = _state.value
        if (s.loading || s.loadingMore || !s.hasMore) return
        load(append = true)
    }

    fun retry() = refresh()

    fun setLayout(layout: FeedLayout) {
        if (_state.value.layout == layout) return
        viewModelScope.launch { setLayoutUseCase(layout) }
    }

    fun toggleFav(movie: Movie) {
        viewModelScope.launch {
            runCatching { toggleFavorite(movie) }
        }
    }

    private fun refresh() = load(append = false)

    private fun load(append: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val snap = _state.value
            val query = snap.query.trim()
            val genreId = snap.genreId
            val sort = snap.sort
            val page = if (append) snap.page + 1 else 1

            _state.update {
                if (append) {
                    it.copy(loadingMore = true, loadMoreError = false, error = null)
                } else {
                    it.copy(
                        loading = true,
                        loadingMore = false,
                        loadMoreError = false,
                        error = null,
                        items = emptyList(),
                        page = 1,
                        totalPages = 1,
                    )
                }
            }

            try {
                val paged = getFeed(
                    query = query.takeIf { it.isNotEmpty() },
                    genreId = genreId,
                    sort = sort,
                    page = page,
                )

                val stillCurrent =
                    _state.value.query.trim() == query &&
                        _state.value.genreId == genreId &&
                        _state.value.sort == sort
                if (!stillCurrent) return@launch

                val merged = if (append) {
                    val seen = _state.value.items.map { it.id }.toSet()
                    _state.value.items + paged.items.filter { it.id !in seen }
                } else {
                    paged.items
                }

                val display = if (query.isNotEmpty() || genreId == null) {
                    sortMovies(merged, sort)
                } else {
                    merged
                }

                _state.update {
                    it.copy(
                        items = display,
                        page = paged.page,
                        totalPages = paged.totalPages,
                        loading = false,
                        loadingMore = false,
                        loadMoreError = false,
                        error = null,
                    )
                }
            } catch (e: Exception) {
                val stillCurrent =
                    _state.value.query.trim() == query &&
                        _state.value.genreId == genreId &&
                        _state.value.sort == sort
                if (!stillCurrent) return@launch

                _state.update {
                    if (append) {
                        it.copy(loadingMore = false, loadMoreError = true)
                    } else {
                        it.copy(
                            loading = false,
                            loadingMore = false,
                            error = e.message ?: "Не удалось загрузить данные.",
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
