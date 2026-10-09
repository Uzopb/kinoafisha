package org.example.kinoafisha.afisha

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class PosterLoadTest {

    @Test
    fun loadsTmdbPosterWithExplicitCioClient() = runBlocking {
        val client = HttpClient(CIO)
        val loader = ImageLoader.Builder(PlatformContext.INSTANCE)
            .components { add(KtorNetworkFetcherFactory(client)) }
            .build()

        val result = loader.execute(
            ImageRequest.Builder(PlatformContext.INSTANCE)
                .data("https://image.tmdb.org/t/p/w342/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg")
                .build(),
        )

        assertIs<SuccessResult>(result, "got ${result::class.simpleName}: $result")
        assertTrue(result.image.width > 0)
        loader.shutdown()
        client.close()
    }

    @Test
    fun loadsTmdbPosterWithDefaultHttpClient() = runBlocking {
        val loader = ImageLoader.Builder(PlatformContext.INSTANCE)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()

        val result = loader.execute(
            ImageRequest.Builder(PlatformContext.INSTANCE)
                .data("https://image.tmdb.org/t/p/w342/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg")
                .build(),
        )

        assertIs<SuccessResult>(result, "got ${result::class.simpleName}: $result")
        loader.shutdown()
    }
}
