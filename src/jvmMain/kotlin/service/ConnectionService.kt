package service

import Navigator
import cn.hutool.http.HttpUtil
import common.HttpCommend
import config.SERVICE_PORT
import currentView
import util.CommendUtil
import java.util.*


/**
 * 连接及指令服务实现
 * @author ShirakawaTyu
 * @since 9/17/2023 5:14 PM
 * @version 1.0
 */
sealed class ConnectionService {

    companion object Default : ConnectionService()

    private var targetIp = ""
    private var serverThread: Thread? = null

    /**
     * 启动指令服务器
     * @author ShirakawaTyu
     */
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

                        HttpCommend.START_SOUND -> SoundStreamService.start(
                            request.getHeader("Mode")
                        )
                        HttpCommend.CLOSE_SOUND -> SoundStreamService.stop()
                        HttpCommend.START_CLIPBOARD -> ClipboardShareService.start()
                        HttpCommend.STOP_CLIPBOARD -> ClipboardShareService.stop()
                        HttpCommend.SEND_FILE -> FileTransferService.receiveFile(
                            request.getHeader("File-Name"),
                            request.getHeader("File-Size").toLong()
                        )
                        HttpCommend.START_KEY_SHARE -> KeyboardShareService.start(
                            request.getHeader("Mode")
                        )
                        HttpCommend.STOP_KEY_SHARE -> KeyboardShareService.stop()
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
                                targetIp = ""
                                currentView.value = Navigator.CONNECT_VIEW
                                SoundStreamService.stop()
                            }
                        }
                    }
                }
            }, 0, 1000)
        }
    }

    /**
     * 连接一个客户端
     * @author ShirakawaTyu
     */
    fun connect(ipAddress: String, callback: (Boolean) -> Unit) {
        CommendUtil.sendCommend(HttpCommend.CONNECT, ipAddress) {
            targetIp = ipAddress
            callback(it)
        }
    }

    /**
     * 断开连接客户端
     * @author ShirakawaTyu
     */
    fun disconnect() {
        targetIp = ""
        currentView.value = Navigator.CONNECT_VIEW
    }

    /**
     * 取得对方IP
     * @author ShirakawaTyu
     */
    fun getTargetIp(): String {
        return targetIp
    }
}
