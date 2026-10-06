package org.example.kinoafisha.core.domain.repository

import org.example.kinoafisha.core.domain.model.Movie

interface MovieRepository {
    suspend fun getNowPlaying(): List<Movie>
}
