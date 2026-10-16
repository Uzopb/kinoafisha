package org.example.kinoafisha.afisha.detailsWithMvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.usecase.GetMovieDetailsUseCase
import org.example.kinoafisha.core.domain.usecase.ObserveFavoritesUseCase
import org.example.kinoafisha.core.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.annotation.KoinViewModel

/**
 * MVI Store экрана деталей:
 * UI → [dispatch(Intent)] → side-effects → [Msg] → Reducer → State → UI
 */
@KoinViewModel
class DetailsViewModelMVI(
    private val getMovieDetails: GetMovieDetailsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsUiState())
    val state: StateFlow<DetailsUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DetailsEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<DetailsEffect> = _effects.asSharedFlow()

    private var favoriteIds: Set<Long> = emptySet()
    private var loadJob: Job? = null

    init {
        observeFavorites()
            .onEach { favs ->
                favoriteIds = favs.map { it.id }.toSet()
                dispatchMsg(DetailsMsg.FavoritesChanged(favoriteIds))
            }
            .launchIn(viewModelScope)
    }

    fun dispatch(intent: DetailsIntent) {
        when (intent) {
            is DetailsIntent.Open -> open(intent.movieId, intent.preview)
            DetailsIntent.Retry -> {
                val id = _state.value.movieId ?: return
                open(id, _state.value.movie)
            }
            DetailsIntent.ToggleFavorite -> {
                val movie = _state.value.movie ?: return
                viewModelScope.launch {
                    runCatching { toggleFavorite(movie) }
                }
            }
        }
    }

    private fun open(movieId: Long, preview: Movie?) {
        loadJob?.cancel()
        dispatchMsg(
            DetailsMsg.OpenRequested(
                movieId = movieId,
                preview = preview,
                favoriteIds = favoriteIds,
            ),
        )
        loadJob = viewModelScope.launch {
            try {
                val details = getMovieDetails(movieId)
                dispatchMsg(
                    DetailsMsg.DetailsLoaded(
                        movieId = movieId,
                        details = details,
                        favoriteIds = favoriteIds,
                    ),
                )
            } catch (_: Exception) {
                dispatchMsg(DetailsMsg.DetailsFailed(movieId))
            }
        }
    }

    private fun dispatchMsg(msg: DetailsMsg) {
        _state.update { DetailsReducer.reduce(it, msg) }
    }
}
