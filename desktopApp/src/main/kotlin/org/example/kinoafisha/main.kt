package org.example.kinoafisha

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.kinoafisha.afisha.App
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
class KinoafishaDesktopApp

fun main() {
    startKoin<KinoafishaDesktopApp> {
        printLogger()
        properties(
            mapOf(
                "tmdb_api_key" to (
                    System.getProperty("tmdb.api.key")
                        ?: System.getenv("TMDB_API_KEY")
                        ?: ""
                    ),
            ),
        )
    }

    val appIcon = BitmapPainter(useResource("ic_app_512.png") { loadImageBitmap(it) })

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Kinoafisha",
            icon = appIcon,
        ) {
            App()
        }
    }
}
