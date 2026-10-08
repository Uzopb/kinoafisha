package org.example.kinoafisha.core.data.mapper

import org.example.kinoafisha.core.domain.model.SortOrder

/** Domain [SortOrder] -> TMDB `sort_by` для `/discover/movie`. */
fun SortOrder.toTmdbSortBy(): String =
    when (this) {
        SortOrder.Popularity -> "popularity.desc"
        SortOrder.RatingDesc -> "vote_average.desc"
        SortOrder.RatingAsc -> "vote_average.asc"
        SortOrder.YearDesc -> "primary_release_date.desc"
        SortOrder.YearAsc -> "primary_release_date.asc"
    }
