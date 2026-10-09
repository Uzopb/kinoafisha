package org.example.kinoafisha.afisha.image

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun createImageHttpClient(): HttpClient = HttpClient(OkHttp)
