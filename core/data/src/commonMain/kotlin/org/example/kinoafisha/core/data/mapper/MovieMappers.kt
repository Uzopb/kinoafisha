package org.example.kinoafisha.core.data.mapper

import org.example.kinoafisha.core.database.entity.MovieEntity
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.network.dto.MovieDto

private const val TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w500"

fun MovieDto.toEntity(): MovieEntity =
    MovieEntity(
        id = id,
        title = title,
        posterUrl = posterPath?.let { "$TMDB_IMAGE_BASE$it" },
    )

fun MovieDto.toDomain(): Movie =
    Movie(
        id = id,
        title = title,
        posterUrl = posterPath?.let { "$TMDB_IMAGE_BASE$it" },
    )

fun MovieEntity.toDomain(): Movie =
    Movie(
        id = id,
        title = title,
        posterUrl = posterUrl,
    )
