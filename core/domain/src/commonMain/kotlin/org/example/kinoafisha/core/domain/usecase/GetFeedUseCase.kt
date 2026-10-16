package org.example.kinoafisha.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.kinoafisha.core.domain.model.FeedKey
import org.example.kinoafisha.core.domain.model.FeedSnapshot
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.koin.core.annotation.Factory

@Factory
class GetFeedUseCase(
    private val repository: MovieRepository,
) {
    fun observe(key: FeedKey): Flow<FeedSnapshot> = repository.observeFeed(key)

    suspend fun sync(key: FeedKey, page: Int = 1, append: Boolean = false) =
        repository.syncFeed(key, page, append)
}
