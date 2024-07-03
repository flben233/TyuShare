package service.transmission

import cn.hutool.http.HttpUtil
import cn.hutool.http.server.SimpleServer
import config.SERVICE_PORT
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Payload
import model.PayloadType
import service.ClipboardShareService
import service.ConnectionService
import java.nio.charset.StandardCharsets

sealed class HttpService {
    companion object Default: HttpService()

    private lateinit var server: SimpleServer
    private val httpPort = SERVICE_PORT

    /**
     * 启动服务器
     * @author ShirakawaTyu
     */
    fun startServer() {
        server = HttpUtil.createServer(httpPort)
        Thread {
            server.addAction("/") { request, response ->
                val data = Json.decodeFromString<Payload>(request.body)
                when (data.payloadType) {
                    PayloadType.CONNECTION -> ConnectionService.handleCommend(
                        Json.decodeFromString(data.payloadJson),
                        request.httpExchange.remoteAddress.hostString)
                    PayloadType.CLIPBOARD -> ClipboardShareService.handleClipboard(data.payloadJson)
                }
                response.sendOk()
            }
            server.start()
        }.start()
    }

    fun sendPayload(payload: Payload, targetIp: String = ConnectionService.getTargetIp()) {
        if (targetIp.isNotEmpty()) {
            HttpUtil.createPost("http://${targetIp}:$httpPort")
                .body(Json.encodeToString<Payload>(payload))
                .charset(StandardCharsets.UTF_8)
                .execute()
        }
    }
}
