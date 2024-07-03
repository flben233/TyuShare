package service

import Navigator
import common.HttpCommend
import currentView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Commend
import util.CommendUtil


/**
 * 连接及指令服务实现
 * @author ShirakawaTyu
 * @since 9/17/2023 5:14 PM
 * @version 1.0
 */
sealed class ConnectionService {

    companion object Default : ConnectionService()

    private var targetIp = ""

    /**
     * 处理指令
     * @author ShirakawaTyu
     */
    fun handleCommend(commend: Commend, fromIp: String) {
        val addition = commend.addition
        when (commend.commend) {
            HttpCommend.CONNECT -> {
                targetIp = fromIp
                currentView.value = Navigator.MAIN_VIEW
                startHeart()
            }
            HttpCommend.START_SOUND -> SoundStreamService.start(addition["Mode"]!!)
            HttpCommend.CLOSE_SOUND -> SoundStreamService.stop()
            HttpCommend.START_CLIPBOARD -> ClipboardShareService.start()
            HttpCommend.STOP_CLIPBOARD -> ClipboardShareService.stop()
            HttpCommend.SEND_FILE -> FileTransferService.receiveFile(addition["File-Name"]!!, addition["File-Size"]!!.toLong())
            HttpCommend.START_KEY_SHARE -> KeyboardShareService.start(addition["Mode"]!!)
            HttpCommend.STOP_KEY_SHARE -> KeyboardShareService.stop()
        }
    }

    private fun startHeart() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                CommendUtil.sendCommend(HttpCommend.HEART) {
                    if (!it) {
                        targetIp = ""
                        currentView.value = Navigator.CONNECT_VIEW
                        SoundStreamService.stop()
                    }
                }
                delay(1000)
            }
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
