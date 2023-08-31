package util

import cn.hutool.http.HttpUtil
import config.SERVICE_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import service.ConnectionService


sealed class CommendUtil {

    companion object Default : CommendUtil()

    fun sendCommend(commend: String, ipAddress: String = ConnectionService.getTargetIp(), callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                HttpUtil.post("http://$ipAddress:$SERVICE_PORT", commend)
                callback(true)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    fun sendCommend(
        commend: String,
        ipAddress: String = ConnectionService.getTargetIp(),
        headers: Map<String, String>,
        callback: (Boolean) -> Unit
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val createPost = HttpUtil.createPost("http://$ipAddress:$SERVICE_PORT")
                createPost.body(commend)
                createPost.addHeaders(headers)
                createPost.execute()
            } catch (e: Exception) {
                callback(false)
            }
            callback(true)
        }
    }
}
