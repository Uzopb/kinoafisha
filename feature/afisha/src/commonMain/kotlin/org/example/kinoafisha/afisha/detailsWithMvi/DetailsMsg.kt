package org.example.kinoafisha.afisha.detailsWithMvi

import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.MovieDetails

/**
 * Внутренние сообщения Store → Reducer.
 * Intent'ы с экрана и результаты side-effect'ов сводятся сюда.
 */
sealed interface DetailsMsg {
    data class OpenRequested(
        val movieId: Long,
        val preview: Movie?,
        val favoriteIds: Set<Long>,
    ) : DetailsMsg

    data class DetailsLoaded(
        val movieId: Long,
        val details: MovieDetails,
        val favoriteIds: Set<Long>,
    ) : DetailsMsg

    data class DetailsFailed(
        val movieId: Long,
    ) : DetailsMsg

    data class FavoritesChanged(
        val favoriteIds: Set<Long>,
    ) : DetailsMsg
}
