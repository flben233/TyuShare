package service

import applicationSetting
import common.HttpCommend
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


sealed class SoundStreamService : BidirectionalService {

    private val soundPort = SERVICE_PORT + 1
    private var udpSocket: DatagramSocket = DatagramSocket(soundPort)
    private var soundThread: Thread? = null
    private val audioFormat = AudioFormat(48000.0f, 16, 2, true, false)
    private var nByte = 0
    private val bufSize = 64
    private val buffer = ByteArray(bufSize)
    private val executor = Executors.newSingleThreadScheduledExecutor()

    companion object Default : SoundStreamService()

    init {
        udpSocket.soTimeout = 1000
    }

    override fun start() {
        applicationSetting.soundStreamStatus.value = true
        if (applicationSetting.soundStreamMode.value == SoundStreamMode.LISTENER) {
            playSound()
        } else {
            sendSound()
        }
    }

    override fun sendCommendAndStart() {
        CommendUtil.sendCommend(HttpCommend.START_SOUND, callback = {
            if (it) {
                start()
            }
        })
    }

    override fun stop() {
        applicationSetting.soundStreamStatus.value = false
        soundThread?.interrupt()
        soundThread = null
    }

    override fun sendCommendAndStop() {
        CommendUtil.sendCommend(HttpCommend.CLOSE_SOUND, callback = {
            if (it) {
                stop()
            }
        })
    }

    fun restart() {
        CommendUtil.sendCommend(HttpCommend.CLOSE_SOUND, callback = {
            if (it) {
                stop()
                sendCommendAndStart()
            }
        })
    }

    private fun playSound() {
        val sourceInfo = DataLine.Info(SourceDataLine::class.java, audioFormat, AudioSystem.NOT_SPECIFIED)
        val sourceDataLine = AudioSystem.getLine(sourceInfo) as SourceDataLine
        sourceDataLine.open(audioFormat)
        sourceDataLine.start()
        var packet = DatagramPacket(ByteArray(bufSize), bufSize)
        soundThread = Thread {
            try {
                udpSocket.receive(packet)
                while (packet.length > 0 && applicationSetting.soundStreamStatus.value) {
                    var ready = false
                    executor.execute {
                        val newPacket = DatagramPacket(ByteArray(bufSize), bufSize)
                        udpSocket.receive(newPacket)
                        packet = newPacket
                        ready = true
                    }
                    sourceDataLine.write(packet.data, 0, packet.length)
                    while (!ready) Thread.onSpinWait()
                }
                sourceDataLine.stop()
                sourceDataLine.close()
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e.stackTrace)
            }
        }
        soundThread!!.start()
    }

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
                    CoroutineScope(Dispatchers.IO).launch {
                        udpSocket.send(
                            DatagramPacket(
                                buffer,
                                nByte,
                                InetSocketAddress(ConnectionService.getTargetIp(), soundPort)
                            )
                        )
                    }
                }
                targetDataLine.stop()
                targetDataLine.close()
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e.stackTrace)
            }
        }
        soundThread!!.start()
    }

    private fun getLine(info: Line.Info): TargetDataLine {
        val mixerInfo = AudioSystem.getMixerInfo()
        for (info1 in mixerInfo) {
            try {
                if (info1.name.contains("CABLE Output (VB-Audio")) {
                    return AudioSystem.getMixer(info1).getLine(info) as TargetDataLine
                }
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e.stackTrace)
            }
        }
        throw RuntimeException("Cannot find virtual audio device.")
    }
}
