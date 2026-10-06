package org.example.kinoafisha.di

import org.example.kinoafisha.Platform
import org.example.kinoafisha.getPlatform
import org.koin.dsl.module

val platformModule = module {
    single<Platform> { getPlatform() }
}