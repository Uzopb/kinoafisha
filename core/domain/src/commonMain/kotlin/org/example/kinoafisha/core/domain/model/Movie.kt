package org.example.kinoafisha.core.domain.model

data class Movie(
    val id: Long,
    val title: String,
    val overview: String = "",
    val releaseDate: String? = null,
    val year: Int? = null,
    val genreIds: List<Int> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val rating: Double = 0.0,
    val voteCount: Int = 0,
    val posterPath: String? = null,
    /** Локальный снимок постера из БД; источник для офлайн UI. */
    val posterBytes: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Movie
        return id == other.id &&
            title == other.title &&
            overview == other.overview &&
            releaseDate == other.releaseDate &&
            year == other.year &&
            genreIds == other.genreIds &&
            genres == other.genres &&
            rating == other.rating &&
            voteCount == other.voteCount &&
            posterPath == other.posterPath &&
            posterBytesEqual(posterBytes, other.posterBytes)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + overview.hashCode()
        result = 31 * result + (releaseDate?.hashCode() ?: 0)
        result = 31 * result + (year ?: 0)
        result = 31 * result + genreIds.hashCode()
        result = 31 * result + genres.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + voteCount
        result = 31 * result + (posterPath?.hashCode() ?: 0)
        result = 31 * result + (posterBytes?.contentHashCode() ?: 0)
        return result
    }
}

private fun posterBytesEqual(a: ByteArray?, b: ByteArray?): Boolean = when {
    a === b -> true
    a == null || b == null -> false
    else -> a.contentEquals(b)
}
