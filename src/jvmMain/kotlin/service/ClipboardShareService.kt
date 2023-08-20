package service

import cn.hutool.core.swing.clipboard.ClipboardUtil
import cn.hutool.http.HttpUtil
import cn.hutool.http.server.SimpleServer
import common.HttpCommend
import config.SERVICE_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.CommendUtil
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

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
                    ClipboardUtil.setStr(request.body)
                    response.sendOk()
                }
                server!!.start()
            }
            serverCoroutine!!.start()
            ClipboardUtil.listen({ _, contents ->
                HttpUtil.post("http://${ConnectionService.getTargetIp()}:$clipPort/clipboard", ClipboardUtil.getStr())
                contents
            }, false)
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