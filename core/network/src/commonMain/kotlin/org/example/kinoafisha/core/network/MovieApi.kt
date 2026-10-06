package org.example.kinoafisha.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.example.kinoafisha.core.network.dto.MovieDto
import org.example.kinoafisha.core.network.dto.NowPlayingResponse
import org.koin.core.annotation.Property
import org.koin.core.annotation.Singleton

@Singleton
class MovieApi(
    private val client: HttpClient,
    @Property("tmdb_api_key") private val apiKey: String,
) {

    suspend fun fetchNowPlaying(): List<MovieDto> =
        client.get("https://api.themoviedb.org/3/movie/now_playing") {
            parameter("api_key", apiKey)
            parameter("language", "ru-RU")
        }.body<NowPlayingResponse>().results
}
