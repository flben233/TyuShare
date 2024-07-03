package util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Commend
import model.Payload
import model.PayloadType
import service.transmission.HttpService


sealed class CommendUtil {

    companion object Default : CommendUtil()

    fun sendCommend(commendStr: String, callback: (Boolean) -> Unit) {
        val commend = Json.encodeToString(Commend(commendStr, mapOf()))
        CoroutineScope(Dispatchers.Default).launch {
            try {
                HttpService.sendPayload(Payload(PayloadType.CONNECTION, commend))
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    fun sendCommend(commendStr: String, targetIp: String, callback: (Boolean) -> Unit) {
        val commend = Json.encodeToString(Commend(commendStr, mapOf()))
        CoroutineScope(Dispatchers.Default).launch {
            try {
                HttpService.sendPayload(Payload(PayloadType.CONNECTION, commend), targetIp)
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    fun sendCommend(
        commendStr: String,
        addition: Map<String, String>,
        callback: (Boolean) -> Unit
    ) {
        val commend = Json.encodeToString(Commend(commendStr, addition))
        CoroutineScope(Dispatchers.Default).launch {
            try {
                HttpService.sendPayload(Payload(PayloadType.CONNECTION, commend))
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }
}
