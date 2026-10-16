package org.example.kinoafisha.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Офлайн-снимок карточки ленты / деталей.
 * Пополняется при успешном fetch ленты и экрана деталей.
 */
@Entity(tableName = "movies")
data class MovieEntity(
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
    val certification: String? = null,
    val director: String? = null,
    /** Через запятую: "Actor One,Actor Two" — без TypeConverter, маппинг в data. */
    val cast: String = "",
)
