package org.example.kinoafisha.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NowPlayingResponse(
    val results: List<MovieDto>,
)

@Serializable
data class MovieDto(
    val id: Long,
    val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
)
