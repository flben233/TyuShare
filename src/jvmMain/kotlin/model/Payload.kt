package model

import kotlinx.serialization.Serializable

@Serializable
data class Payload(
    val payloadType: String,
    val payloadJson: String
)

interface PayloadType {
    companion object {
        const val KEY_ACTION = "KA"
        const val CONNECTION = "CONN"
        const val CLIPBOARD = "CB"
    }
}
