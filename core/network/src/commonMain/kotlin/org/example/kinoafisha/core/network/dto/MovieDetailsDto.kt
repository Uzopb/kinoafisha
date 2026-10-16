package org.example.kinoafisha.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsDto(
    val id: Long,
    val title: String,
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    val genres: List<GenreDto> = emptyList(),
    val reviews: ReviewsAppendDto? = null,
    @SerialName("release_dates") val releaseDates: ReleaseDatesAppendDto? = null,
    val credits: CreditsAppendDto? = null,
)

@Serializable
data class CreditsAppendDto(
    val cast: List<CastMemberDto> = emptyList(),
    val crew: List<CrewMemberDto> = emptyList(),
)

@Serializable
data class CastMemberDto(
    val id: Long,
    val name: String,
    val order: Int = Int.MAX_VALUE,
)

@Serializable
data class CrewMemberDto(
    val id: Long,
    val name: String,
    val job: String = "",
    val department: String = "",
)

@Serializable
data class ReviewsAppendDto(
    val results: List<ReviewDto> = emptyList(),
)

@Serializable
data class ReviewDto(
    val id: String,
    val author: String = "",
    @SerialName("author_details") val authorDetails: AuthorDetailsDto? = null,
    val content: String = "",
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class AuthorDetailsDto(
    val name: String? = null,
    val username: String? = null,
    val rating: Double? = null,
)

@Serializable
data class ReleaseDatesAppendDto(
    val results: List<CountryReleaseDatesDto> = emptyList(),
)

@Serializable
data class CountryReleaseDatesDto(
    @SerialName("iso_3166_1") val countryCode: String,
    @SerialName("release_dates") val releaseDates: List<ReleaseDateDto> = emptyList(),
)

@Serializable
data class ReleaseDateDto(
    val certification: String = "",
)
