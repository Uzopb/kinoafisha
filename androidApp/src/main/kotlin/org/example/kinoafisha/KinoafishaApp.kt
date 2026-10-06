package org.example.kinoafisha

import android.app.Application
import org.example.kinoafisha.afisha.di.afishaModule
import org.example.kinoafisha.core.data.di.dataModule
import org.example.kinoafisha.core.database.di.databaseModule
import org.example.kinoafisha.core.domain.di.domainModule
import org.example.kinoafisha.core.network.di.networkModule
import org.example.kinoafisha.di.platformModule
import org.koin.core.context.GlobalContext.startKoin

class KinoafishaApp: Application() {
    override fun onCreate() {
        super.onCreate()
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
    }
}