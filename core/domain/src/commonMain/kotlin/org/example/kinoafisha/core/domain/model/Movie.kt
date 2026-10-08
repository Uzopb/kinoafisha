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
)
