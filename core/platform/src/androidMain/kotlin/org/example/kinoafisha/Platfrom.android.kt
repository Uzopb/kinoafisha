package org.example.kinoafisha

import android.os.Build

actual fun getPlatform(): Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}