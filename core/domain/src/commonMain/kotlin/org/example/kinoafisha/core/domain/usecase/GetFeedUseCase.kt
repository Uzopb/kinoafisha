package org.example.kinoafisha.core.domain.usecase

import org.example.kinoafisha.core.domain.model.PagedMovies
import org.example.kinoafisha.core.domain.model.SortOrder
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.koin.core.annotation.Factory

@Factory
class GetFeedUseCase(
    private val repository: MovieRepository,
) {
    suspend operator fun invoke(
        query: String? = null,
        genreId: Int? = null,
        sort: SortOrder = SortOrder.Popularity,
        page: Int = 1,
    ): PagedMovies = repository.getFeed(query, genreId, sort, page)
}
