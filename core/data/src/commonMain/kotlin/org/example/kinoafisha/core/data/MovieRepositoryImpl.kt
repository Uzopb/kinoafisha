package org.example.kinoafisha.core.data

import org.example.kinoafisha.core.data.mapper.toDomain
import org.example.kinoafisha.core.data.mapper.toEntity
import org.example.kinoafisha.core.database.dao.MovieDao
import org.example.kinoafisha.core.database.entity.MovieEntity
import org.example.kinoafisha.core.domain.model.Movie
import org.example.kinoafisha.core.domain.repository.MovieRepository
import org.example.kinoafisha.core.network.MovieApi
import org.example.kinoafisha.core.network.dto.MovieDto
import org.koin.core.annotation.Singleton

@Singleton
class MovieRepositoryImpl(
    private val api: MovieApi,
    private val movieDao: MovieDao,
) : MovieRepository {

    override suspend fun getNowPlaying(): List<Movie> {
        movieDao.getAll().takeIf { it.isNotEmpty() }
            ?.let { return it.map(MovieEntity::toDomain) }

        val remote = api.fetchNowPlaying()
        movieDao.upsertAll(remote.map(MovieDto::toEntity))
        return remote.map(MovieDto::toDomain)
    }
}
