package org.example.kinoafisha.core.database.di

import org.example.kinoafisha.core.database.AppDatabase
import org.example.kinoafisha.core.database.getRoomDatabase
import org.koin.dsl.module

val databaseModule = module {
    includes(platformDatabaseModule)
    single { getRoomDatabase(get()) }
    single { get<AppDatabase>().movieDao() }
}
