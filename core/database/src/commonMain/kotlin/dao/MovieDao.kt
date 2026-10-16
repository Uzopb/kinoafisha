package org.example.kinoafisha.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.example.kinoafisha.core.database.entity.MovieEntity

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    suspend fun getAll(): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getById(id: Long): MovieEntity?

    @Query("SELECT * FROM movies WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<MovieEntity>

    @Upsert
    suspend fun upsert(movie: MovieEntity)

    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun clear()
}
