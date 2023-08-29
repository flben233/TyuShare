package service

import applicationSetting
import common.HttpCommend
import component.tool.SoundStreamMode
import config.SERVICE_PORT
import util.CommendUtil
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import javax.sound.sampled.*


sealed class SoundStreamService {

    private val soundPort = SERVICE_PORT + 1
    private var udpSocket: DatagramSocket = DatagramSocket(soundPort)
    private var soundThread: Thread? = null
    private val audioFormat = AudioFormat(48000.0f, 16, 2, true, false)
    private var nByte = 0
    private val bufSize = 128
    private val buffer = ByteArray(bufSize)
    private var sourceDataLine: SourceDataLine? = null
    private var targetDataLine: TargetDataLine? = null

    companion object Default : SoundStreamService()

    init {
        val sourceInfo = DataLine.Info(SourceDataLine::class.java, audioFormat, AudioSystem.NOT_SPECIFIED)
        val targetInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)
        sourceDataLine = AudioSystem.getLine(sourceInfo) as SourceDataLine
        targetDataLine = getLine(targetInfo)
        sourceDataLine!!.open(audioFormat)
        targetDataLine!!.open(audioFormat)
        sourceDataLine!!.start()
        targetDataLine!!.start()
        udpSocket.soTimeout = 1000
    }

    fun start() {
        applicationSetting.soundStreamStatus.value = true
        if (applicationSetting.soundStreamMode.value == SoundStreamMode.LISTENER) {
            playSound()
        } else {
            sendSound()
        }
    }

    fun sendCommendAndStart() {
        CommendUtil.sendCommend(HttpCommend.START_SOUND, callback = {
            if (it) {
                start()
            }
        })
    }

    fun stop() {
        applicationSetting.soundStreamStatus.value = false
        soundThread?.interrupt()
        soundThread = null
    }

    fun sendCommendAndStop() {
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
        val packet = DatagramPacket(buffer, bufSize)
        soundThread = Thread {
            try {
                udpSocket.receive(packet)
                while (packet.length > 0 && applicationSetting.soundStreamStatus.value) {
                    sourceDataLine!!.write(buffer, 0, packet.length)
                    udpSocket.receive(packet)
                }
            } catch (_: Exception) {
            }
        }
        soundThread!!.start()
    }

    private fun sendSound() {
        soundThread = Thread {
            try {
                while ((targetDataLine!!.read(buffer, 0, bufSize).also { nByte = it } > 0)
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
            } catch (_: Exception) {
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
            } catch (_: Exception) {
            }
        }
        throw RuntimeException("Cannot find virtual audio device.")
    }
}
