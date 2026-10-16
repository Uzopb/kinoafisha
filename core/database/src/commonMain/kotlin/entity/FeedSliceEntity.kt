package org.example.kinoafisha.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Индекс среза ленты: storageKey → упорядоченные id фильмов.
 * Сами карточки лежат в [MovieEntity], постеры — в [PosterEntity].
 */
@Entity(tableName = "feed_slices")
data class FeedSliceEntity(
    @PrimaryKey val key: String,
    /** Через запятую: "11,22,33" */
    val movieIds: String,
    val page: Int,
    val totalPages: Int,
    val syncedAt: Long,
)
