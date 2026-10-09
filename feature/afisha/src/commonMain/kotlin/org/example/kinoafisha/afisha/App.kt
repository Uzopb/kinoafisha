package org.example.kinoafisha.afisha

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import org.example.kinoafisha.afisha.image.createImageHttpClient
import org.example.kinoafisha.afisha.theme.KinoTheme
import org.example.kinoafisha.afisha.ui.navigation.AfishaShell

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        val httpClient = createImageHttpClient()
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient))
            }
            .crossfade(true)
            .build()
    }

    KinoTheme {
        AfishaShell()
    }
}
