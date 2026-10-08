package org.example.kinoafisha.core.domain.usecase

import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.koin.core.annotation.Factory

@Factory
class ToggleFavoriteUseCase(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(movie: Movie) = repository.toggleFavorite(movie)
}
