package org.example.kinoafisha.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.example.kinoafisha.core.network.dto.GenreListResponse
import org.example.kinoafisha.core.network.dto.MovieDetailsDto
import org.example.kinoafisha.core.network.dto.MovieDto
import org.example.kinoafisha.core.network.dto.PagedResponse
import org.koin.core.annotation.Property
import org.koin.core.annotation.Singleton

@Singleton
class MovieApi(
    private val client: HttpClient,
    @Property("tmdb_api_key") private val apiKey: String,
) {

    suspend fun nowPlaying(page: Int = 1): PagedResponse<MovieDto> =
        client.get("$BASE/movie/now_playing") {
            tmdbDefaults()
            parameter("region", "RU")
            parameter("page", page)
        }.body()

    suspend fun search(query: String, page: Int = 1): PagedResponse<MovieDto> =
        client.get("$BASE/search/movie") {
            tmdbDefaults()
            parameter("query", query)
            parameter("include_adult", false)
            parameter("page", page)
        }.body()

    suspend fun discover(
        genreId: Int? = null,
        sortBy: String,
        dateFrom: String? = null,
        dateTo: String? = null,
        voteCountGte: Int? = null,
        page: Int = 1,
    ): PagedResponse<MovieDto> =
        client.get("$BASE/discover/movie") {
            tmdbDefaults()
            parameter("sort_by", sortBy)
            parameter("include_adult", false)
            parameter("page", page)
            genreId?.let { parameter("with_genres", it) }
            dateFrom?.let { parameter("primary_release_date.gte", it) }
            dateTo?.let { parameter("primary_release_date.lte", it) }
            voteCountGte?.let { parameter("vote_count.gte", it) }
            if (dateFrom != null || dateTo != null) {
                parameter("region", "RU")
            }
        }.body()

    suspend fun genres(): GenreListResponse =
        client.get("$BASE/genre/movie/list") {
            tmdbDefaults()
        }.body()

    suspend fun movieDetails(id: Long): MovieDetailsDto =
        client.get("$BASE/movie/$id") {
            tmdbDefaults()
            parameter("append_to_response", "reviews,release_dates")
        }.body()

    private fun HttpRequestBuilder.tmdbDefaults() {
        parameter("api_key", apiKey)
        parameter("language", "ru-RU")
    }

    private companion object {
        const val BASE = "https://api.themoviedb.org/3"
    }
}
