package org.example.kinoafisha.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.example.kinoafisha.core.database.entity.PosterEntity

@Dao
interface PosterDao {
    @Query("SELECT * FROM posters WHERE movieId = :movieId")
    suspend fun getById(movieId: Long): PosterEntity?

    @Query("SELECT * FROM posters WHERE movieId IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<PosterEntity>

    @Upsert
    suspend fun upsert(poster: PosterEntity)

    @Upsert
    suspend fun upsertAll(posters: List<PosterEntity>)
}
