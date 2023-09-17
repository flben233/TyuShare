package service

import androidx.compose.ui.window.Notification
import applicationSetting
import cn.hutool.http.HttpUtil
import cn.hutool.http.server.SimpleServer
import common.HttpCommend
import config.SERVICE_PORT
import kotlinx.coroutines.*
import service.interfaces.BidirectionalService
import tray
import util.ClipboardUtil
import util.CommendUtil
import util.LoggerUtil


/**
 * 剪贴板共享服务逻辑实现
 * @author ShirakawaTyu
 * @since 9/17/2023 5:09 PM
 * @version 1.0
 */
sealed class ClipboardShareService: BidirectionalService {
    companion object Default : ClipboardShareService()

    private var server: SimpleServer? = null
    private val clipPort = SERVICE_PORT + 2
    private var serverCoroutine: Job? = null
    private var clipboardCoroutine: Job? = null

    /**
     * 向对方发送启动剪贴板共享服务请求然后启动自身服务
     * @author ShirakawaTyu
     */
    override fun sendCommendAndStart() {
        CommendUtil.sendCommend(HttpCommend.START_CLIPBOARD) {
            start()
        }
    }

    /**
     * 启动剪贴板共享服务，一般来说这个方法只用于接收到启动请求时调用
     * @author ShirakawaTyu
     */
    override fun start() {
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
            startListen()
        }
    }

    /**
     * 停止剪贴板共享服务，一般来说只用于接收到请求后调用
     * @author ShirakawaTyu
     */
    override fun stop() {
        server?.rawServer?.stop(1)
        serverCoroutine?.cancel()
        serverCoroutine = null
        clipboardCoroutine?.cancel()
        clipboardCoroutine = null
    }

    /**
     * 发送停止指令然后停止剪贴板共享服务
     * @author ShirakawaTyu
     */
    override fun sendCommendAndStop() {
        CommendUtil.sendCommend(HttpCommend.STOP_CLIPBOARD) {
            stop()
        }
    }

    /**
     * 开始监听剪贴板，这里使用轮询实现而不是监听器
     * 原因是java自带的监听器遇到隐藏字符时常常失效
     * @author ShirakawaTyu
     */
    private fun startListen() {
        if (clipboardCoroutine == null) {
            clipboardCoroutine = CoroutineScope(Dispatchers.Default).launch {
                var lastString: String? = ClipboardUtil.getStr()
                while (applicationSetting.clipboardStatus.value) {
                    if (ConnectionService.getTargetIp().length > 1) {
                        try {
                            val contentStr = ClipboardUtil.getStr()
                            if (contentStr != lastString && !contentStr.isNullOrEmpty()) {
                                HttpUtil.post(
                                    "http://${ConnectionService.getTargetIp()}:$clipPort/clipboard",
                                    contentStr
                                )
                                lastString = contentStr
                            }
                        } catch (e: Exception) {
                            LoggerUtil.logStackTrace(e)
                        }
                    }
                    delay(100)
                }
            }
        }
    }
}
