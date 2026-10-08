package org.example.kinoafisha.core.domain.model

data class MovieDetails(
    val movie: Movie,
    val certification: String? = null,
    val reviews: List<Review> = emptyList(),
)
