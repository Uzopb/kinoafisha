package org.example.kinoafisha.core.domain.usecase

import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.koin.core.annotation.Factory

@Factory
class GetGenresUseCase(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(): List<Genre> = repository.getGenres()
}
