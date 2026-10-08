package org.example.kinoafisha.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Офлайн-снимок избранного.
 * Не зависит от кэша ленты [MovieEntity].
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val overview: String = "",
    val releaseDate: String? = null,
    val year: Int? = null,
    /** Через запятую: "28,12,878" — без TypeConverter, маппинг в data. */
    val genreIds: String = "",
    val rating: Double = 0.0,
    val voteCount: Int = 0,
    val posterPath: String? = null,
    val addedAt: Long = 0L,
)
