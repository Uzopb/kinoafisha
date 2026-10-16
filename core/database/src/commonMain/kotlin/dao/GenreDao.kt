package org.example.kinoafisha.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.example.kinoafisha.core.database.entity.GenreEntity

@Dao
interface GenreDao {
    @Query("SELECT * FROM genres ORDER BY name ASC")
    suspend fun getAll(): List<GenreEntity>

    @Upsert
    suspend fun upsertAll(genres: List<GenreEntity>)
}
