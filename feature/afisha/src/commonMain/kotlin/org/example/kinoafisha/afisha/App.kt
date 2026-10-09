package org.example.kinoafisha.afisha

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import io.ktor.client.HttpClient
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.ui.navigation.AfishaNav
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun App() {
    val imageHttpClient = koinInject<HttpClient>(named("images"))

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(imageHttpClient))
            }
            .crossfade(true)
            .build()
    }

    KinoTheme {
        AfishaNav()
    }
}
