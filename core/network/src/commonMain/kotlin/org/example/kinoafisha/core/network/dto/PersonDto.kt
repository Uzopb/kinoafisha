package org.example.kinoafisha.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    val id: Long,
    val name: String,
    @SerialName("known_for_department") val knownForDepartment: String? = null,
    val popularity: Double = 0.0,
)
