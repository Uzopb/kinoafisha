package org.example.kinoafisha.core.network.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton

@Module(includes = [PlatformNetworkModule::class])
@Configuration
@ComponentScan("org.example.kinoafisha.core.network")
class NetworkModule {

    /** TMDB JSON API — Accept/ContentNegotiation под JSON. */
    @Singleton
    fun httpClient(engine: HttpClientEngine): HttpClient =
        HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

    /**
     * Coil / image CDN — тот же engine (один пул соединений),
     * без ContentNegotiation, чтобы не слать Accept: application/json.
     */
    @Singleton
    @Named("images")
    fun imageHttpClient(engine: HttpClientEngine): HttpClient = HttpClient(engine)
}
