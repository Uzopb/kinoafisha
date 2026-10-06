package org.example.kinoafisha.core.database.di

import androidx.room.RoomDatabase
import org.example.kinoafisha.core.database.AppDatabase
import org.example.kinoafisha.core.database.dao.MovieDao
import org.example.kinoafisha.core.database.getRoomDatabase
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module(includes = [PlatformDatabaseModule::class])
@Configuration
class DatabaseModule {

    @Singleton
    fun appDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase =
        getRoomDatabase(builder)

    @Singleton
    fun movieDao(db: AppDatabase): MovieDao = db.movieDao()
}
