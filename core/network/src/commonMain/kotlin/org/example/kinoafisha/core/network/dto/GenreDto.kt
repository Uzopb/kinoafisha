package org.example.kinoafisha.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    val id: Int,
    val name: String,
)

@Serializable
data class GenreListResponse(
    val genres: List<GenreDto> = emptyList(),
)
