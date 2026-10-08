package org.example.kinoafisha.core.data.mapper

import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.model.SortOrder

/** Клиентская сортировка карточек */
fun sortMovies(list: List<Movie>, sort: SortOrder): List<Movie> =
    when (sort) {
        SortOrder.RatingDesc ->
            list.sortedWith(
                compareByDescending<Movie> { it.rating }
                    .thenByDescending { it.voteCount },
            )
        SortOrder.RatingAsc ->
            list.sortedWith(
                compareBy<Movie> { it.rating }
                    .thenBy { it.voteCount },
            )
        SortOrder.YearDesc ->
            list.sortedByDescending { it.releaseDate.orEmpty() }
        SortOrder.YearAsc ->
            list.sortedBy { it.releaseDate.orEmpty() }
        SortOrder.Popularity -> list
    }
