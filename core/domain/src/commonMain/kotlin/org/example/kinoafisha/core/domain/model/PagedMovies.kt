package org.example.kinoafisha.core.domain.model

data class PagedMovies(
    val items: List<Movie>,
    val page: Int,
    val totalPages: Int,
)
