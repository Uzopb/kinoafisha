package org.example.kinoafisha.core.domain.usecase

import org.example.kinoafisha.core.domain.model.FeedLayout
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.koin.core.annotation.Factory

@Factory
class SetLayoutUseCase(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(layout: FeedLayout) = repository.setLayout(layout)
}
