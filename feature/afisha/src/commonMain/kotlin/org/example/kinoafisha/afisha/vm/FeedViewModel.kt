package org.example.kinoafisha.afisha.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kinoafisha.core.domain.model.FeedKey
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.FeedSnapshot
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.SearchMode
import org.example.kinoafisha.core.domain.model.SortOrder
import org.example.kinoafisha.core.domain.usecase.GetFeedUseCase
import org.example.kinoafisha.core.domain.usecase.GetGenresUseCase
import org.example.kinoafisha.core.domain.usecase.ObserveFavoritesUseCase
import org.example.kinoafisha.core.domain.usecase.ObserveLayoutUseCase
import org.example.kinoafisha.core.domain.usecase.SetLayoutUseCase
import org.example.kinoafisha.core.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.annotation.KoinViewModel

data class FeedState(
    val query: String = "",
    val searchMode: SearchMode = SearchMode.Title,
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
    val hasError: Boolean = false,
    val favoriteIds: Set<Long> = emptySet(),
) {
    val hasMore: Boolean get() = page < totalPages && items.isNotEmpty()
}

@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class FeedViewModel(
    private val getFeed: GetFeedUseCase,
    private val getGenres: GetGenresUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val observeLayout: ObserveLayoutUseCase,
    private val setLayoutUseCase: SetLayoutUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val searchMode = MutableStateFlow(SearchMode.Title)
    private val genreId = MutableStateFlow<Int?>(null)
    private val sort = MutableStateFlow(SortOrder.Popularity)
    private val loadingMore = MutableStateFlow(false)
    private val loadMoreError = MutableStateFlow(false)
    private val syncError = MutableStateFlow(false)

    private val feedKeyFlow = combine(query, genreId, sort, searchMode) { q, g, s, mode ->
        FeedKey(query = q, genreId = g, sort = s, searchMode = mode)
    }.distinctUntilChanged()

    private val feedKey: StateFlow<FeedKey> =
        feedKeyFlow.stateIn(viewModelScope, SharingStarted.Eagerly, FeedKey())

    private val feedSnapshot: StateFlow<FeedSnapshot> = feedKeyFlow
        .flatMapLatest { key -> getFeed.observe(key) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, FeedSnapshot(key = FeedKey()))

    private val uiExtras = combine(
        getGenres.observe(),
        observeLayout(),
        observeFavorites().map { favs -> favs.map { it.id }.toSet() },
        loadingMore,
        loadMoreError,
    ) { genres, layout, favoriteIds, more, moreErr ->
        UiExtras(genres, layout, favoriteIds, more, moreErr)
    }

    val state: StateFlow<FeedState> = combine(
        feedKey,
        feedSnapshot,
        uiExtras,
        syncError,
    ) { key, snap, extras, err ->
        FeedState(
            query = key.query,
            searchMode = key.searchMode,
            genreId = key.genreId,
            genres = extras.genres,
            sort = key.sort,
            page = snap.page,
            totalPages = snap.totalPages,
            layout = extras.layout,
            items = snap.items,
            loading = snap.refreshing && snap.items.isEmpty(),
            loadingMore = extras.loadingMore,
            loadMoreError = extras.loadMoreError,
            hasError = err && snap.items.isEmpty(),
            favoriteIds = extras.favoriteIds,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, FeedState(loading = true))

    private var searchJob: Job? = null
    private var syncJob: Job? = null

    init {
        feedKeyFlow
            .onEach { key -> sync(key, page = 1, append = false) }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            runCatching { getGenres.sync() }
        }
    }

    fun onQueryChange(value: String) {
        query.update { value }
        if (value.isNotBlank()) genreId.value = null
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            sync(feedKey.value, page = 1, append = false)
        }
    }

    fun clearQuery() {
        searchJob?.cancel()
        query.value = ""
        sync(feedKey.value, page = 1, append = false)
    }

    fun setSearchMode(mode: SearchMode) {
        if (searchMode.value == mode) return
        searchMode.value = mode
    }

    fun setSort(value: SortOrder) {
        if (sort.value == value) return
        sort.value = value
    }

    fun selectGenre(id: Int?) {
        searchJob?.cancel()
        val current = genreId.value
        genreId.value = if (id != null && id == current) null else id
        query.value = ""
    }

    fun resetFiltersAndRefresh() {
        searchJob?.cancel()
        genreId.value = null
        query.value = ""
        searchMode.value = SearchMode.Title
        sync(feedKey.value, page = 1, append = false)
    }

    fun loadMore() {
        val snap = feedSnapshot.value
        if (state.value.loading || loadingMore.value || !snap.hasMore) return
        sync(feedKey.value, page = snap.page + 1, append = true)
    }

    fun retry() = sync(feedKey.value, page = 1, append = false)

    fun setLayout(value: FeedLayout) {
        if (state.value.layout == value) return
        viewModelScope.launch { setLayoutUseCase(value) }
    }

    fun toggleFav(movie: Movie) {
        viewModelScope.launch {
            runCatching { toggleFavorite(movie) }
        }
    }

    private fun sync(key: FeedKey, page: Int, append: Boolean) {
        if (!append) syncJob?.cancel()
        val job = viewModelScope.launch {
            if (append) {
                loadingMore.value = true
                loadMoreError.value = false
            } else {
                syncError.value = false
            }
            try {
                getFeed.sync(key, page = page, append = append)
                syncError.value = false
                loadMoreError.value = false
            } catch (_: Exception) {
                if (append) {
                    loadMoreError.value = true
                } else {
                    syncError.value = feedSnapshot.value.items.isEmpty()
                }
            } finally {
                if (append) loadingMore.value = false
            }
        }
        if (!append) syncJob = job
    }

    private data class UiExtras(
        val genres: List<Genre>,
        val layout: FeedLayout,
        val favoriteIds: Set<Long>,
        val loadingMore: Boolean,
        val loadMoreError: Boolean,
    )

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
