package org.example.kinoafisha.afisha.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.Review
import org.example.kinoafisha.core.domain.usecase.GetMovieDetailsUseCase
import org.example.kinoafisha.core.domain.usecase.ObserveFavoritesUseCase
import org.example.kinoafisha.core.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.annotation.KoinViewModel

data class DetailsState(
    val movieId: Long? = null,
    val movie: Movie? = null,
    val certification: String? = null,
    val reviews: List<Review> = emptyList(),
    val reviewsLoading: Boolean = false,
    val reviewsError: Boolean = false,
    val loading: Boolean = false,
    val hasError: Boolean = false,
    val isFavorite: Boolean = false,
)

@KoinViewModel
class DetailsViewModel(
    private val getMovieDetails: GetMovieDetailsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    private var favoriteIds: Set<Long> = emptySet()
    private var loadJob: Job? = null

    init {
        observeFavorites()
            .onEach { favs ->
                favoriteIds = favs.map { it.id }.toSet()
                val id = _state.value.movieId
                if (id != null) {
                    _state.update { it.copy(isFavorite = id in favoriteIds) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun open(movieId: Long, preview: Movie? = null) {
        loadJob?.cancel()
        _state.value = DetailsState(
            movieId = movieId,
            movie = preview?.takeIf { it.id == movieId },
            reviewsLoading = true,
            loading = preview == null || preview.id != movieId,
            isFavorite = movieId in favoriteIds,
        )
        loadJob = viewModelScope.launch {
            try {
                val details = getMovieDetails(movieId)
                if (_state.value.movieId != movieId) return@launch
                _state.update {
                    it.copy(
                        movie = details.movie,
                        certification = details.certification,
                        reviews = details.reviews,
                        reviewsLoading = false,
                        reviewsError = false,
                        loading = false,
                        hasError = false,
                        isFavorite = movieId in favoriteIds,
                    )
                }
            } catch (_: Exception) {
                if (_state.value.movieId != movieId) return@launch
                _state.update {
                    it.copy(
                        loading = false,
                        reviewsLoading = false,
                        reviewsError = true,
                        hasError = it.movie == null,
                    )
                }
            }
        }
    }

    fun toggleFav() {
        val movie = _state.value.movie ?: return
        viewModelScope.launch {
            runCatching { toggleFavorite(movie) }
        }
    }

    fun retry() {
        val id = _state.value.movieId ?: return
        open(id, _state.value.movie)
    }
}
