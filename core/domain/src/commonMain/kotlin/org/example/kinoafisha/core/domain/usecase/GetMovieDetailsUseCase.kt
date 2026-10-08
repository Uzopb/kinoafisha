package org.example.kinoafisha.core.domain.usecase

import org.example.kinoafisha.core.domain.model.MovieDetails
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.koin.core.annotation.Factory

@Factory
class GetMovieDetailsUseCase(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(id: Long): MovieDetails = repository.getMovieDetails(id)
}
