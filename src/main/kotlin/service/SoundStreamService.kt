package service

import applicationSetting
import common.HttpCommend
import component.tool.SoundStreamMode
import service.interfaces.BidirectionalService
import util.CommendUtil
import util.ProcessUtil


/**
 * 音频串流服务实现
 * @author ShirakawaTyu
 * @since 9/17/2023 5:20 PM
 * @version 1.0
 */
sealed class SoundStreamService : BidirectionalService {

    private var audioProcess: Process? = null
    private var audioWatcher: Thread? = null
    private val resourcesDir = System.getProperty("compose.application.resources.dir")
    companion object Default : SoundStreamService()

    fun start(mode: String) {
        applicationSetting.soundStreamMode.value = if (mode == "1") SoundStreamMode.SPEAKER else SoundStreamMode.LISTENER
        start()
    }

    /**
     * 启动音频串流服务，这里会根据当前的设定决定发送数据还是播放音频
     * 一般在接收到请求后调用
     * @author ShirakawaTyu
     */
    override fun start() {
        if (audioProcess != null && audioProcess!!.isAlive) {
            return
        }
        applicationSetting.soundStreamStatus.value = true
        audioProcess = if (applicationSetting.soundStreamMode.value == SoundStreamMode.LISTENER)
            ProcessUtil.startProcess("$resourcesDir\\audio-exporter.exe", "--mode=client", "--address=${ConnectionService.getTargetIp()}")
        else ProcessUtil.startProcess("$resourcesDir\\audio-exporter.exe", "--mode=server")
        audioWatcher = Thread {
            audioProcess!!.waitFor()
            if (applicationSetting.soundStreamStatus.value) {
                restart()
            }
        }
        audioWatcher!!.start()
    }

    /**
     * 发送启动音频串流服务请求并打开自身服务
     * @author ShirakawaTyu
     */
    override fun sendCommendAndStart() {
        val mode = if (applicationSetting.soundStreamMode.value == SoundStreamMode.LISTENER) "1"
        else "0"
        if (applicationSetting.soundStreamMode.value == SoundStreamMode.LISTENER) {
            CommendUtil.sendCommend(HttpCommend.START_SOUND, mapOf("Mode" to mode)) {
                if (it) {
                    start()
                }
            }
        } else {
            start()
            CommendUtil.sendCommend(HttpCommend.START_SOUND, mapOf("Mode" to mode)) {}
        }
    }

    /**
     * 停止音频串流服务，一般在接收到请求后调用
     * @author ShirakawaTyu
     */
    override fun stop() {
        applicationSetting.soundStreamStatus.value = false
        if (audioProcess != null) {
            ProcessUtil.killProcess(audioProcess!!)
            audioProcess = null
        }
    }

    /**
     * 发送停止音频串流服务请求并停止自身服务
     * @author ShirakawaTyu
     */
    override fun sendCommendAndStop() {
        CommendUtil.sendCommend(HttpCommend.CLOSE_SOUND, callback = {
            if (it) {
                stop()
            }
        })
    }

    /**
     * 重启服务
     * @author ShirakawaTyu
     */
    fun restart() {
        CommendUtil.sendCommend(HttpCommend.CLOSE_SOUND, callback = {
            if (it) {
                stop()
                sendCommendAndStart()
            }
        })
    }
}
