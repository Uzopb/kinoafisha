package org.example.kinoafisha.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.example.kinoafisha.core.database.dao.AppSettingsDao
import org.example.kinoafisha.core.database.dao.FavoriteDao
import org.example.kinoafisha.core.database.dao.MovieDao
import org.example.kinoafisha.core.database.entity.AppSettingsEntity
import org.example.kinoafisha.core.database.entity.FavoriteEntity
import org.example.kinoafisha.core.database.entity.MovieEntity

@Database(
    entities = [
        MovieEntity::class,
        FavoriteEntity::class,
        AppSettingsEntity::class,
    ],
    version = 2,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun appSettingsDao(): AppSettingsDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
