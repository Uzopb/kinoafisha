package org.example.kinoafisha.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.koin.core.annotation.Factory

@Factory
class ObserveFavoritesUseCase(
    private val repository: MovieRepository,
) {
    operator fun invoke(): Flow<List<Movie>> = repository.observeFavorites()
}
