package org.example.kinoafisha.afisha.detailsWithMvi

/**
 * Чистая функция: State + Msg → State.
 * Без корутин, use case'ов и Android API.
 */
object DetailsReducer {

    fun reduce(state: DetailsUiState, msg: DetailsMsg): DetailsUiState =
        when (msg) {
            is DetailsMsg.OpenRequested -> {
                val preview = msg.preview?.takeIf { it.id == msg.movieId }
                DetailsUiState(
                    movieId = msg.movieId,
                    movie = preview,
                    reviewsLoading = true,
                    loading = preview == null,
                    isFavorite = msg.movieId in msg.favoriteIds,
                )
            }

            is DetailsMsg.DetailsLoaded -> {
                if (state.movieId != msg.movieId) state
                else state.copy(
                    movie = msg.details.movie,
                    certification = msg.details.certification,
                    reviews = msg.details.reviews,
                    reviewsLoading = false,
                    reviewsError = false,
                    loading = false,
                    hasError = false,
                    isFavorite = msg.movieId in msg.favoriteIds,
                )
            }

            is DetailsMsg.DetailsFailed -> {
                if (state.movieId != msg.movieId) state
                else state.copy(
                    loading = false,
                    reviewsLoading = false,
                    reviewsError = true,
                    hasError = state.movie == null,
                )
            }

            is DetailsMsg.FavoritesChanged -> {
                val id = state.movieId
                if (id == null) state
                else state.copy(isFavorite = id in msg.favoriteIds)
            }
        }
}
