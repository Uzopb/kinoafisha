package org.example.kinoafisha.core.database.di

import androidx.room.RoomDatabase
import org.example.kinoafisha.core.database.AppDatabase
import org.example.kinoafisha.core.database.getDatabaseBuilder
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
actual class PlatformDatabaseModule {

    @Singleton
    fun databaseBuilder(): RoomDatabase.Builder<AppDatabase> =
        getDatabaseBuilder()
}
