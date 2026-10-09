package org.example.kinoafisha.afisha.image

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

actual fun createImageHttpClient(): HttpClient = HttpClient(CIO)
