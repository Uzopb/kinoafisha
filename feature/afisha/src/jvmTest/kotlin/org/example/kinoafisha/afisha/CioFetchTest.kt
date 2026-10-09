package org.example.kinoafisha.afisha

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpHeaders
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

class CioFetchTest {
    @Test
    fun cioFetch() = runBlocking {
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 30_000
            }
        }
        try {
            val bytes = client.get("https://image.tmdb.org/t/p/w342/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg") {
                header(HttpHeaders.UserAgent, "Kinoafisha/1.0")
            }.readRawBytes()
            println("CIO bytes=${bytes.size}")
            assertTrue(bytes.size > 1000)
        } finally {
            client.close()
        }
    }

    @Test
    fun javaEngineFetch() = runBlocking {
        val client = HttpClient(Java)
        try {
            val bytes = client.get("https://image.tmdb.org/t/p/w342/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg") {
                header(HttpHeaders.UserAgent, "Kinoafisha/1.0")
            }.readRawBytes()
            println("Java engine bytes=${bytes.size}")
            assertTrue(bytes.size > 1000)
        } finally {
            client.close()
        }
    }
}
