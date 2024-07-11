package service

import androidx.compose.ui.window.Notification
import applicationSetting
import common.HttpCommend
import kotlinx.coroutines.*
import model.Payload
import model.PayloadType
import service.interfaces.BidirectionalService
import service.transmission.HttpService
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

    private var clipboardCoroutine: Job? = null
    private var started: Boolean = false

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
        started = true
        startListen()
    }

    /**
     * 停止剪贴板共享服务，一般来说只用于接收到请求后调用
     * @author ShirakawaTyu
     */
    override fun stop() {
        clipboardCoroutine?.cancel()
        clipboardCoroutine = null
        started = false
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

    fun handleClipboard(clipboard: String) {
        if (clipboard != ClipboardUtil.getStr() && clipboard.isNotEmpty()) {
            ClipboardUtil.setStr(clipboard)
            tray.sendNotification(Notification("剪贴板", clipboard))
        }
    }

    /**
     * 开始监听剪贴板，这里使用轮询实现而不是监听器
     * 原因是java自带的监听器遇到隐藏字符时常常失效
     * @author ShirakawaTyu
     */
    private fun startListen() {
        if (clipboardCoroutine?.isActive == true) {
            return
        }
        clipboardCoroutine = CoroutineScope(Dispatchers.Default).launch {
            var lastString: String = ClipboardUtil.getStr()
            while (applicationSetting.clipboardStatus.value) {
                try {
                    val contentStr = ClipboardUtil.getStr()
                    if (contentStr != lastString && contentStr.isNotEmpty()) {
                        HttpService.sendPayload(Payload(PayloadType.CLIPBOARD, contentStr))
                        lastString = contentStr
                    }
                } catch (e: Exception) {
                    LoggerUtil.logStackTrace(e)
                }
                delay(100)
            }
        }
    }
}
