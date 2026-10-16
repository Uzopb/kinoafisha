package org.example.kinoafisha.core.data.mapper

import org.example.kinoafisha.core.database.entity.FavoriteEntity
import org.example.kinoafisha.core.database.entity.GenreEntity
import org.example.kinoafisha.core.database.entity.MovieEntity
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.MovieDetails
import org.example.kinoafisha.core.domain.model.Review
import org.example.kinoafisha.core.network.dto.GenreDto
import org.example.kinoafisha.core.network.dto.MovieDetailsDto
import org.example.kinoafisha.core.network.dto.MovieDto
import org.example.kinoafisha.core.network.dto.ReviewDto

fun GenreDto.toDomain(): Genre = Genre(id = id, name = name)

fun GenreDto.toEntity(): GenreEntity = GenreEntity(id = id, name = name)

fun GenreEntity.toDomain(): Genre = Genre(id = id, name = name)

fun MovieDto.toEntity(): MovieEntity {
    val date = releaseDate
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        releaseDate = date,
        year = date?.take(4)?.toIntOrNull(),
        genreIds = genreIds.joinToString(","),
        rating = voteAverage,
        voteCount = voteCount,
        posterPath = posterPath,
    )
}

fun MovieDto.toDomain(
    genreById: Map<Int, Genre> = emptyMap(),
    posterBytes: ByteArray? = null,
): Movie {
    val date = releaseDate
    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = date,
        year = date?.take(4)?.toIntOrNull(),
        genreIds = genreIds,
        genres = genreIds.mapNotNull { genreById[it] },
        rating = voteAverage,
        voteCount = voteCount,
        posterPath = posterPath,
        posterBytes = posterBytes,
    )
}

fun MovieEntity.toDomain(
    genreById: Map<Int, Genre> = emptyMap(),
    posterBytes: ByteArray? = null,
): Movie {
    val ids = parseGenreIds(genreIds)
    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        year = year,
        genreIds = ids,
        genres = ids.mapNotNull { genreById[it] },
        rating = rating,
        voteCount = voteCount,
        posterPath = posterPath,
        posterBytes = posterBytes,
        director = director,
        cast = parseNames(cast),
    )
}

fun MovieEntity.toDetails(
    genreById: Map<Int, Genre> = emptyMap(),
    posterBytes: ByteArray? = null,
): MovieDetails =
    MovieDetails(
        movie = toDomain(genreById, posterBytes),
        certification = certification,
        reviews = emptyList(),
    )

fun Movie.toFavoriteEntity(addedAt: Long): FavoriteEntity =
    FavoriteEntity(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        year = year,
        genreIds = genreIds.joinToString(","),
        rating = rating,
        voteCount = voteCount,
        posterPath = posterPath,
        director = director,
        cast = cast.joinToString(","),
        addedAt = addedAt,
    )

fun FavoriteEntity.toDomain(
    genreById: Map<Int, Genre> = emptyMap(),
    posterBytes: ByteArray? = null,
): Movie {
    val ids = parseGenreIds(genreIds)
    return Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        year = year,
        genreIds = ids,
        genres = ids.mapNotNull { genreById[it] },
        rating = rating,
        voteCount = voteCount,
        posterPath = posterPath,
        posterBytes = posterBytes,
        director = director,
        cast = parseNames(cast),
    )
}

fun FavoriteEntity.toDetails(
    genreById: Map<Int, Genre> = emptyMap(),
    posterBytes: ByteArray? = null,
): MovieDetails =
    MovieDetails(
        movie = toDomain(genreById, posterBytes),
        reviews = emptyList(),
    )

fun MovieDetailsDto.toDomain(posterBytes: ByteArray? = null): MovieDetails {
    val resolvedGenres = genres.map { it.toDomain() }
    val (director, cast) = pickCredits()
    val movie = Movie(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        year = releaseDate?.take(4)?.toIntOrNull(),
        genreIds = resolvedGenres.map { it.id },
        genres = resolvedGenres,
        rating = voteAverage,
        voteCount = voteCount,
        posterPath = posterPath,
        posterBytes = posterBytes,
        director = director,
        cast = cast,
    )
    return MovieDetails(
        movie = movie,
        certification = pickCertification(),
        reviews = reviews?.results.orEmpty().map { it.toDomain() },
    )
}

fun MovieDetailsDto.toEntity(): MovieEntity {
    val date = releaseDate
    val (director, cast) = pickCredits()
    return MovieEntity(
        id = id,
        title = title,
        overview = overview,
        releaseDate = date,
        year = date?.take(4)?.toIntOrNull(),
        genreIds = genres.map { it.id }.joinToString(","),
        rating = voteAverage,
        voteCount = voteCount,
        posterPath = posterPath,
        certification = pickCertification(),
        director = director,
        cast = cast.joinToString(","),
    )
}

fun ReviewDto.toDomain(): Review =
    Review(
        id = id,
        author = authorDetails?.name?.takeIf { it.isNotBlank() }
            ?: author.takeIf { it.isNotBlank() }
            ?: "",
        rating = authorDetails?.rating,
        createdAt = createdAt,
        content = content,
    )

fun MovieDetailsDto.pickCertification(): String? {
    val list = releaseDates?.results.orEmpty()
    val preferred = list.find { it.countryCode == "RU" }
        ?: list.find { it.countryCode == "US" }
    return preferred?.releaseDates
        ?.firstOrNull { it.certification.isNotBlank() }
        ?.certification
}

fun MovieDetailsDto.pickCredits(): Pair<String?, List<String>> {
    val credits = credits ?: return null to emptyList()
    val director = credits.crew
        .firstOrNull { it.job.equals("Director", ignoreCase = true) }
        ?.name
        ?.takeIf { it.isNotBlank() }
    val cast = credits.cast
        .sortedBy { it.order }
        .map { it.name.trim() }
        .filter { it.isNotEmpty() }
        .distinct()
        .take(CAST_STORAGE_LIMIT)
    return director to cast
}

/** Сохраняет credits/certification при upsert карточки из ленты без details. */
fun MovieEntity.withPreservedDetailsFrom(previous: MovieEntity?): MovieEntity {
    if (previous == null) return this
    return copy(
        certification = certification ?: previous.certification,
        director = director ?: previous.director,
        cast = cast.ifBlank { previous.cast },
    )
}

private fun parseGenreIds(raw: String): List<Int> =
    raw.split(',')
        .mapNotNull { it.trim().toIntOrNull() }

private fun parseNames(raw: String): List<String> =
    raw.split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

private const val CAST_STORAGE_LIMIT = 12
