package example.com.model

import kotlinx.serialization.Serializable

@Serializable
data class SpeakerDto(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val description: String
)
