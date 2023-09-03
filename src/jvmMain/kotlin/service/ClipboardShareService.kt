package service

import androidx.compose.ui.window.Notification
import cn.hutool.http.HttpUtil
import cn.hutool.http.server.SimpleServer
import common.HttpCommend
import config.SERVICE_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tray
import util.ClipboardUtil
import util.CommendUtil
import util.LoggerUtil

sealed class ClipboardShareService {
    companion object Default : ClipboardShareService()

    private var server: SimpleServer? = null
    private val clipPort = SERVICE_PORT + 2
    private var serverCoroutine: Job? = null
    fun startShare() {
        if (serverCoroutine == null) {
            server = HttpUtil.createServer(clipPort)
            serverCoroutine = CoroutineScope(Dispatchers.Default).launch {
                server!!.addAction("/clipboard") { request, response ->
                    if (request.body != ClipboardUtil.getStr() && request.body.isNotEmpty()) {
                        ClipboardUtil.setStr(request.body)
                        tray.sendNotification(Notification("剪贴板", request.body))
                    }
                    response.sendOk()
                }
                server!!.start()
            }
            serverCoroutine!!.start()
            ClipboardUtil.listen {
                if (ConnectionService.getTargetIp().length > 1) {
                    try {
                        val contentStr = ClipboardUtil.getStr()
                        if (!contentStr.isNullOrEmpty()) {
                            HttpUtil.post(
                                "http://${ConnectionService.getTargetIp()}:$clipPort/clipboard",
                                contentStr
                            )
                        }
                    } catch (e: Exception) {
                        LoggerUtil.logStackTrace(e.stackTrace)
                    }
                }
            }
        }
    }

    fun stopShareServer() {
        server?.rawServer?.stop(1)
        serverCoroutine = null
    }

    fun stopShare() {
        CommendUtil.sendCommend(HttpCommend.STOP_CLIPBOARD) {
            stopShareServer()
        }
    }

}