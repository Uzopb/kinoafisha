package org.example.kinoafisha.afisha.detailsWithMvi

import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.Review

/** Иммутабельный снимок экрана деталей для Compose. */
data class DetailsUiState(
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
