package service

import Navigator
import cn.hutool.http.HttpUtil
import common.HttpCommend
import config.SERVICE_PORT
import currentView
import util.CommendUtil
import java.util.*


sealed class ConnectionService {

    companion object Default : ConnectionService()

    private var targetIp = ""
    private var serverThread: Thread? = null

    fun startCommendServer() {
        if (serverThread == null) {
            val server = HttpUtil.createServer(SERVICE_PORT)
            serverThread = Thread {
                server.addAction("/") { request, response ->
                    when (request.body) {
                        HttpCommend.CONNECT -> {
                            targetIp = request.httpExchange.remoteAddress.hostString
                            currentView.value = Navigator.MAIN_VIEW
                        }

                        HttpCommend.START_SOUND -> SoundStreamService.startSoundServer()
                        HttpCommend.CLOSE_SOUND -> SoundStreamService.closeSocket()
                        HttpCommend.START_CLIPBOARD -> ClipboardShareService.startShare()
                        HttpCommend.STOP_CLIPBOARD -> ClipboardShareService.stopShareServer()
                        HttpCommend.SEND_FILE -> FileTransferService.receiveFile(request.getHeader("File-Name"))
                    }
                    response.sendOk()
                }
                server.start()
            }
            serverThread!!.start()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    if (targetIp.length > 1) {
                        CommendUtil.sendCommend(HttpCommend.HEART) {
                            if (!it) {
                                currentView.value = Navigator.CONNECT_VIEW
                                SoundStreamService.closeSocket()
                            }
                        }
                    }
                }
            }, 0, 1000)
        }
    }

    fun connect(ipAddress: String, callback: (Boolean) -> Unit) {
        CommendUtil.sendCommend(HttpCommend.CONNECT, ipAddress) {
            targetIp = ipAddress
            callback(it)
        }
    }

    fun disconnect() {
        targetIp = ""
        currentView.value = Navigator.CONNECT_VIEW
    }

    fun getTargetIp(): String {
        return targetIp
    }
}



