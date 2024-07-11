package model

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionItem(
    val ipAddress: String,
    val lastDate: String
)