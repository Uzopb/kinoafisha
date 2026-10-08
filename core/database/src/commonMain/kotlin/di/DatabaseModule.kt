package org.example.kinoafisha.core.database.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.example.kinoafisha.core.database.AppDatabase
import org.example.kinoafisha.core.database.dao.AppSettingsDao
import org.example.kinoafisha.core.database.dao.FavoriteDao
import org.example.kinoafisha.core.database.dao.MovieDao
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module(includes = [PlatformDatabaseModule::class])
@Configuration
class DatabaseModule {

    @Singleton
    fun appDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase =
        builder
            .fallbackToDestructiveMigration(dropAllTables = true)
            .setDriver(BundledSQLiteDriver())
            .build() // query context по умолчанию — Dispatchers.IO

    @Singleton
    fun movieDao(db: AppDatabase): MovieDao = db.movieDao()

    @Singleton
    fun favoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()

    @Singleton
    fun appSettingsDao(db: AppDatabase): AppSettingsDao = db.appSettingsDao()
}
