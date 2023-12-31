package service

import applicationSetting
import common.HttpCommend
import component.tool.KeyboardMode
import component.tool.SoundStreamMode
import config.SERVICE_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import service.interfaces.BidirectionalService
import util.CommendUtil
import util.LoggerUtil
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import javax.sound.sampled.*


/**
 * 音频串流服务实现
 * @author ShirakawaTyu
 * @since 9/17/2023 5:20 PM
 * @version 1.0
 */
sealed class SoundStreamService : BidirectionalService {

    // 音频串流UDP端口
    private val soundPort = SERVICE_PORT + 1

    private var udpSocket: DatagramSocket = DatagramSocket(soundPort)
    private var soundThread: Thread? = null
    private val audioFormat = AudioFormat(48000.0f, 16, 2, true, false)
    private var nByte = 0
    private val bufSize = 128
    private val buffer = ByteArray(bufSize)

    companion object Default : SoundStreamService()

    init {
        udpSocket.soTimeout = 1000
    }

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
        applicationSetting.soundStreamStatus.value = true
        if (applicationSetting.soundStreamMode.value == SoundStreamMode.LISTENER) {
            playSound()
        } else {
            sendSound()
        }
    }

    /**
     * 发送启动音频串流服务请求并打开自身服务
     * @author ShirakawaTyu
     */
    override fun sendCommendAndStart() {
        val header = if (applicationSetting.soundStreamMode.value == SoundStreamMode.LISTENER) "1"
        else "0"
        CommendUtil.sendCommend(HttpCommend.START_SOUND, headers = mapOf("Mode" to header), callback = {
            if (it) {
                start()
            }
        })
    }

    /**
     * 停止音频串流服务，一般在接收到请求后调用
     * @author ShirakawaTyu
     */
    override fun stop() {
        applicationSetting.soundStreamStatus.value = false
        soundThread?.interrupt()
        soundThread = null
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

    /**
     * 播放对方传入的音频数据
     * @author ShirakawaTyu
     */
    private fun playSound() {
        val sourceInfo = DataLine.Info(SourceDataLine::class.java, audioFormat, AudioSystem.NOT_SPECIFIED)
        val sourceDataLine = AudioSystem.getLine(sourceInfo) as SourceDataLine
        sourceDataLine.open(audioFormat)
        sourceDataLine.start()
        val packet = DatagramPacket(ByteArray(bufSize), bufSize)
        soundThread = Thread {
            try {
                while (packet.length > 0 && applicationSetting.soundStreamStatus.value) {
                    udpSocket.receive(packet)
                    sourceDataLine.write(packet.data, 0, packet.length)
                }
                sourceDataLine.stop()
                sourceDataLine.close()
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e)
            }
        }
        soundThread!!.start()
    }

    /**
     * 发送音频数据
     * @author ShirakawaTyu
     */
    private fun sendSound() {
        val targetInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)
        val targetDataLine = getLine(targetInfo)
        targetDataLine.open(audioFormat)
        targetDataLine.start()
        soundThread = Thread {
            try {
                while ((targetDataLine.read(buffer, 0, bufSize).also { nByte = it } > 0)
                        && applicationSetting.soundStreamStatus.value
                ) {
                    udpSocket.send(
                        DatagramPacket(
                            buffer,
                            nByte,
                            InetSocketAddress(ConnectionService.getTargetIp(), soundPort)
                        )
                    )
                }
                targetDataLine.stop()
                targetDataLine.close()
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e)
            }
        }
        soundThread!!.start()
    }

    /**
     * 取得音频输入设备
     * @author ShirakawaTyu
     */
    private fun getLine(info: Line.Info): TargetDataLine {
        val mixerInfo = AudioSystem.getMixerInfo()
        for (info1 in mixerInfo) {
            try {
                if (info1.name.contains("CABLE Output (VB-Audio")) {
                    return AudioSystem.getMixer(info1).getLine(info) as TargetDataLine
                }
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e)
            }
        }
        throw RuntimeException("Cannot find virtual audio device.")
    }
}
