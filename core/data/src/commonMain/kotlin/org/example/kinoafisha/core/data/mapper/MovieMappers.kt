package org.example.kinoafisha.core.data.mapper

import org.example.kinoafisha.core.database.entity.FavoriteEntity
import org.example.kinoafisha.core.database.entity.MovieEntity
import org.example.kinoafisha.core.domain.model.Genre
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.MovieDetails
import org.example.kinoafisha.core.domain.model.Review
import org.example.kinoafisha.core.network.dto.GenreDto
import org.example.kinoafisha.core.network.dto.MovieDetailsDto
import org.example.kinoafisha.core.network.dto.MovieDto
import org.example.kinoafisha.core.network.dto.ReviewDto

private const val TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w500"

fun GenreDto.toDomain(): Genre = Genre(id = id, name = name)

fun MovieDto.toEntity(): MovieEntity =
    MovieEntity(
        id = id,
        title = title,
        posterUrl = posterPath?.let { "$TMDB_IMAGE_BASE$it" },
    )

fun MovieDto.toDomain(genreById: Map<Int, Genre> = emptyMap()): Movie {
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
    )
}

fun MovieEntity.toDomain(): Movie =
    Movie(
        id = id,
        title = title,
        posterPath = posterUrl,
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
        addedAt = addedAt,
    )

fun FavoriteEntity.toDomain(genreById: Map<Int, Genre> = emptyMap()): Movie {
    val ids = genreIds
        .split(',')
        .mapNotNull { it.trim().toIntOrNull() }
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
    )
}

fun MovieDetailsDto.toDomain(): MovieDetails {
    val resolvedGenres = genres.map { it.toDomain() }
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
    )
    return MovieDetails(
        movie = movie,
        certification = pickCertification(),
        reviews = reviews?.results.orEmpty().map { it.toDomain() },
    )
}

fun ReviewDto.toDomain(): Review =
    Review(
        id = id,
        author = authorDetails?.name?.takeIf { it.isNotBlank() }
            ?: author.takeIf { it.isNotBlank() }
            ?: "Аноним",
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
