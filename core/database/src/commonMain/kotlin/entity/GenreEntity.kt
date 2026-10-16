package org.example.kinoafisha.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Офлайн-справочник жанров TMDB. */
@Entity(tableName = "genres")
data class GenreEntity(
    @PrimaryKey val id: Int,
    val name: String,
)
