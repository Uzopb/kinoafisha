package org.example.kinoafisha

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.kinoafisha.afisha.App
import org.example.kinoafisha.afisha.di.afishaModule
import org.example.kinoafisha.core.data.di.dataModule
import org.example.kinoafisha.core.database.di.databaseModule
import org.example.kinoafisha.core.domain.di.domainModule
import org.example.kinoafisha.core.network.di.networkModule
import org.example.kinoafisha.di.platformModule
import org.koin.core.context.GlobalContext.startKoin

fun main() {

    startKoin {
        modules(
            domainModule,
            networkModule,
            databaseModule,
            dataModule,
            afishaModule,
            platformModule
        )
    }

    application {

        Window(
            onCloseRequest = ::exitApplication,
            title = "Kinoafisha",
        ) {
            App()
        }
    }
}