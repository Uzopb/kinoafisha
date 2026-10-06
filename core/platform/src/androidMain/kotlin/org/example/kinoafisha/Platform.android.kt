package org.example.kinoafisha

import android.os.Build
import org.koin.core.annotation.Singleton

@Singleton
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}
