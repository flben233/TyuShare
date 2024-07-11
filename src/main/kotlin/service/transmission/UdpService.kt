package service.transmission

import config.SERVICE_PORT
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Payload
import model.PayloadType
import service.ConnectionService
import service.KeyboardShareService
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

sealed class UdpService {
    companion object Default: UdpService()
    private val udpSocket = DatagramSocket(SERVICE_PORT)
    private var started = false
    private val bufSize = 65536

    fun startServer() {
        started = true
        val buf = ByteArray(bufSize)
        val packet = DatagramPacket(buf, buf.size)
        Thread {
            while (started) {
                udpSocket.receive(packet)
                val strBytes = packet.data.copyOfRange(0, packet.length)
                val dataJson = String(strBytes, StandardCharsets.UTF_8)
                val payload = Json.decodeFromString<Payload>(dataJson)
                when (payload.payloadType) {
                    PayloadType.KEY_ACTION -> KeyboardShareService.handleKey(Json.decodeFromString(payload.payloadJson))
                }
            }
        }.start()
    }

    fun sendPayload(payload: Payload, targetIp: String = ConnectionService.getTargetIp()) {
        if (targetIp.isEmpty()) {
            return
        }
        val data = Json.encodeToString(payload)
        val packet = DatagramPacket(
            data.toByteArray(StandardCharsets.UTF_8), data.length,
                InetSocketAddress(targetIp, udpSocket.localPort)
        )
        udpSocket.send(packet)
    }
}