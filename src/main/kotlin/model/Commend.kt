package model

import kotlinx.serialization.Serializable

@Serializable
data class Commend(
    val commend: String,
    val addition: Map<String, String>
)