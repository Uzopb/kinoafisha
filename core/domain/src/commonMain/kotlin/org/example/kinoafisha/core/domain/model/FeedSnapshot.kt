package org.example.kinoafisha.core.domain.model

/**
 * Снимок среза для UI: данные всегда локальные.
 * [refreshing] — идёт фоновый sync; [fromCache] — ещё не подтверждено сетью в этой сессии.
 */
data class FeedSnapshot(
    val key: FeedKey,
    val items: List<Movie> = emptyList(),
    val page: Int = 1,
    val totalPages: Int = 1,
    val refreshing: Boolean = false,
    val fromCache: Boolean = true,
) {
    val hasMore: Boolean get() = page < totalPages && items.isNotEmpty()
}
