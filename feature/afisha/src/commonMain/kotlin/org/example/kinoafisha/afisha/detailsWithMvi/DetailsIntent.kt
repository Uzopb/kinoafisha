package org.example.kinoafisha.afisha.detailsWithMvi

import org.example.kinoafisha.core.domain.model.Movie

/** Намерения UI → Store. Единственная точка входа с экрана. */
sealed interface DetailsIntent {
    data class Open(
        val movieId: Long,
        val preview: Movie? = null,
    ) : DetailsIntent

    data object Retry : DetailsIntent

    data object ToggleFavorite : DetailsIntent
}
