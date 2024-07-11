package service.transmission

import com.sun.net.httpserver.HttpServer
import config.SERVICE_PORT
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Payload
import model.PayloadType
import service.ClipboardShareService
import service.ConnectionService
import util.HttpUtil
import java.net.InetSocketAddress

sealed class HttpService {
    companion object Default: HttpService()

    private lateinit var server: HttpServer
    private val httpPort = SERVICE_PORT

    /**
     * 启动服务器
     * @author ShirakawaTyu
     */
    fun startServer() {
        server = HttpServer.create()
        server.bind(InetSocketAddress(httpPort), 0)
        Thread {
            server.createContext("/").setHandler {
                var requestBody: String
                with(it.requestBody.reader()) {
                    requestBody = readText()
                }
                val data = Json.decodeFromString<Payload>(requestBody)
                when (data.payloadType) {
                    PayloadType.CONNECTION -> ConnectionService.handleCommend(
                        Json.decodeFromString(data.payloadJson),
                        it.remoteAddress.hostString)
                    PayloadType.CLIPBOARD -> ClipboardShareService.handleClipboard(data.payloadJson)
                }
                it.sendResponseHeaders(200, -1)
            }
            server.start()
        }.start()
    }

    fun sendPayload(payload: Payload, targetIp: String = ConnectionService.getTargetIp()) {
        if (targetIp.isNotEmpty()) {
            HttpUtil.post("http://${targetIp}:$httpPort", Json.encodeToString(payload))
        }
    }
}
