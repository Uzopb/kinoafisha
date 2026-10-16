package org.example.kinoafisha.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.example.kinoafisha.core.database.dao.AppSettingsDao
import org.example.kinoafisha.core.database.dao.FavoriteDao
import org.example.kinoafisha.core.database.dao.FeedSliceDao
import org.example.kinoafisha.core.database.dao.GenreDao
import org.example.kinoafisha.core.database.dao.MovieDao
import org.example.kinoafisha.core.database.dao.PosterDao
import org.example.kinoafisha.core.database.entity.AppSettingsEntity
import org.example.kinoafisha.core.database.entity.FavoriteEntity
import org.example.kinoafisha.core.database.entity.FeedSliceEntity
import org.example.kinoafisha.core.database.entity.GenreEntity
import org.example.kinoafisha.core.database.entity.MovieEntity
import org.example.kinoafisha.core.database.entity.PosterEntity

@Database(
    entities = [
        MovieEntity::class,
        FavoriteEntity::class,
        GenreEntity::class,
        PosterEntity::class,
        FeedSliceEntity::class,
        AppSettingsEntity::class,
    ],
    version = 6,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun genreDao(): GenreDao
    abstract fun posterDao(): PosterDao
    abstract fun feedSliceDao(): FeedSliceDao
    abstract fun appSettingsDao(): AppSettingsDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
