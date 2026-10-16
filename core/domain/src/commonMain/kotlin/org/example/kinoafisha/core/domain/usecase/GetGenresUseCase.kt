package org.example.kinoafisha.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.koin.core.annotation.Factory

@Factory
class GetGenresUseCase(
    private val repository: MovieRepository,
) {
    fun observe(): Flow<List<Genre>> = repository.observeGenres()

    suspend fun sync() = repository.syncGenres()
}
