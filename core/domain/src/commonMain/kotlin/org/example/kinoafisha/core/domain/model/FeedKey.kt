package org.example.kinoafisha.core.domain.model

/**
 * Ключ среза ленты. Один глобальный список фильмов не используется —
 * каждый фильтр/сортировка/поиск — отдельный индекс id.
 */
data class FeedKey(
    val query: String = "",
    val searchMode: SearchMode = SearchMode.Title,
    val genreId: Int? = null,
    val sort: SortOrder = SortOrder.Popularity,
) {
    fun normalized(): FeedKey = copy(query = query.trim())

    /** Стабильный ключ для Room / memory map. */
    fun storageKey(): String {
        val n = normalized()
        return "q=${n.query}|g=${n.genreId ?: ""}|s=${n.sort.name}|m=${n.searchMode.name}"
    }
}
