package org.example.kinoafisha.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Бессрочный офлайн-снимок постера.
 * [path] — TMDB poster_path; при смене path байты перекачиваются.
 */
@Entity(tableName = "posters")
data class PosterEntity(
    @PrimaryKey val movieId: Long,
    val path: String,
    val bytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as PosterEntity
        return movieId == other.movieId &&
            path == other.path &&
            bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = movieId.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}
