package org.example.kinoafisha.afisha

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

/**
 * Живой smoke-тест TMDB + Coil. Нестабилен в CI (CDN / Skiko).
 * Вручную: снять @Ignore и
 * `./gradlew :feature:afisha:jvmTest --tests '*PosterLoadTest'`
 */
@Ignore
class PosterLoadTest {

    private fun tmdbHttpClient() = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
        defaultRequest {
            header(HttpHeaders.UserAgent, "Kinoafisha/1.0")
        }
    }

    @Test
    fun loadsTmdbPosterWithExplicitCioClient() = runBlocking {
        val client = tmdbHttpClient()
        val loader = ImageLoader.Builder(PlatformContext.INSTANCE)
            .components { add(KtorNetworkFetcherFactory(client)) }
            .build()

        try {
            val result = loader.execute(
                ImageRequest.Builder(PlatformContext.INSTANCE)
                    .data(POSTER_URL)
                    .build(),
            )

            assertIs<SuccessResult>(result, "got ${result::class.simpleName}: $result")
            assertTrue(result.image.width > 0)
        } finally {
            loader.shutdown()
            client.close()
        }
    }

    @Test
    fun loadsTmdbPosterWithConfiguredHttpClient() = runBlocking {
        val client = tmdbHttpClient()
        val loader = ImageLoader.Builder(PlatformContext.INSTANCE)
            .components { add(KtorNetworkFetcherFactory(client)) }
            .build()

        try {
            val result = loader.execute(
                ImageRequest.Builder(PlatformContext.INSTANCE)
                    .data(POSTER_URL)
                    .build(),
            )

            assertIs<SuccessResult>(result, "got ${result::class.simpleName}: $result")
            assertTrue(result.image.width > 0)
        } finally {
            loader.shutdown()
            client.close()
        }
    }

    private companion object {
        const val POSTER_URL =
            "https://image.tmdb.org/t/p/w342/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg"
    }
}
