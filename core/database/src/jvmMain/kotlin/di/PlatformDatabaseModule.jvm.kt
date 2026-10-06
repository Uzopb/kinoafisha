package org.example.kinoafisha.core.database.di

import org.example.kinoafisha.core.database.getDatabaseBuilder
import org.koin.dsl.module

actual val platformDatabaseModule = module {
    single { getDatabaseBuilder() }
}
