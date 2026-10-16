package org.example.kinoafisha.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.example.kinoafisha.core.database.entity.FeedSliceEntity

@Dao
interface FeedSliceDao {
    @Query("SELECT * FROM feed_slices WHERE key = :key")
    suspend fun getByKey(key: String): FeedSliceEntity?

    @Query("SELECT * FROM feed_slices")
    suspend fun getAll(): List<FeedSliceEntity>

    @Upsert
    suspend fun upsert(slice: FeedSliceEntity)
}
