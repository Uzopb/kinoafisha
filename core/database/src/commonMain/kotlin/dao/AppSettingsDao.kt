package org.example.kinoafisha.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.example.kinoafisha.core.database.entity.AppSettingsEntity

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = :id")
    fun observe(id: Int = AppSettingsEntity.ID): Flow<AppSettingsEntity?>

    @Upsert
    suspend fun upsert(settings: AppSettingsEntity)
}
