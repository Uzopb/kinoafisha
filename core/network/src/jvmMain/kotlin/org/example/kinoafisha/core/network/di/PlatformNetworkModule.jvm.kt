package org.example.kinoafisha.core.network.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
actual class PlatformNetworkModule {

    @Singleton
    fun httpClientEngine(): HttpClientEngine = CIO.create()
}
