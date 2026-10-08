package org.example.kinoafisha.core.domain.model

data class Review(
    val id: String,
    val author: String,
    val rating: Double?,
    val createdAt: String?,
    val content: String,
)
