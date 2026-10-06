package org.example.kinoafisha

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
class KinoafishaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<KinoafishaApp> {
            androidContext(this@KinoafishaApp)
            androidLogger()
            properties(mapOf("tmdb_api_key" to BuildConfig.TMDB_API_KEY))
        }
    }
}
