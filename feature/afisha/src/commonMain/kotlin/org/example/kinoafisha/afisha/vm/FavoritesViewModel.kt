package org.example.kinoafisha.afisha.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kinoafisha.afisha.util.sortMovies
import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.SortOrder
import org.example.kinoafisha.core.domain.usecase.ObserveFavoritesUseCase
import org.example.kinoafisha.core.domain.usecase.ObserveLayoutUseCase
import org.example.kinoafisha.core.domain.usecase.SetLayoutUseCase
import org.example.kinoafisha.core.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.annotation.KoinViewModel

data class FavoritesState(
    val rawItems: List<Movie> = emptyList(),
    val items: List<Movie> = emptyList(),
    val sort: SortOrder = SortOrder.Popularity,
    val layout: FeedLayout = FeedLayout.List,
    val favoriteIds: Set<Long> = emptySet(),
)

@KoinViewModel
class FavoritesViewModel(
    private val observeFavorites: ObserveFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val observeLayout: ObserveLayoutUseCase,
    private val setLayoutUseCase: SetLayoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    private val sortFlow = MutableStateFlow(SortOrder.Popularity)

    init {
        combine(observeFavorites(), sortFlow, observeLayout()) { favs, sort, layout ->
            FavoritesState(
                rawItems = favs,
                items = sortMovies(favs, sort),
                sort = sort,
                layout = layout,
                favoriteIds = favs.map { it.id }.toSet(),
            )
        }
            .onEach { next -> _state.value = next }
            .launchIn(viewModelScope)
    }

    fun setSort(sort: SortOrder) {
        sortFlow.value = sort
        _state.update { it.copy(sort = sort, items = sortMovies(it.rawItems, sort)) }
    }

    fun setLayout(layout: FeedLayout) {
        if (_state.value.layout == layout) return
        viewModelScope.launch { setLayoutUseCase(layout) }
    }

    fun toggleFav(movie: Movie) {
        viewModelScope.launch {
            runCatching { toggleFavorite(movie) }
        }
    }
}
