package org.example.kinoafisha

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.kinoafisha.afisha.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kinoafisha",
    ) {
        App()
    }
}