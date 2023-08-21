package service

import applicationSetting
import common.HttpCommend
import component.tool.SoundStreamMode
import config.SERVICE_PORT
import util.CommendUtil
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import javax.sound.sampled.*


sealed class SoundStreamService {

    private var server: ServerSocket? = null
    private var client: Socket? = null
    private var soundThread: Thread? = null
    private val audioFormat = AudioFormat(48000.0f, 16, 2, true, false)
    private var nByte = 0
    private val bufSize = 128
    private val buffer = ByteArray(bufSize)

    companion object Default : SoundStreamService()

    fun stopSoundStream() {
        CommendUtil.sendCommend(HttpCommend.CLOSE_SOUND, callback = {
            closeSocket()
        })
    }

    fun startSoundStream() {
        CommendUtil.sendCommend(HttpCommend.START_SOUND, callback = {
            client = Socket(ConnectionService.getTargetIp(), SERVICE_PORT + 1)
            startSound()
        })
    }

    private fun startSound() {
        soundThread = if (applicationSetting.soundStreamMode.value == SoundStreamMode.LISTENER) {
            playSoundStream(client!!.getInputStream())
        } else {
            sendSystemSound(client!!.getOutputStream())
        }
    }

    fun closeSocket() {
        client?.close()
        server?.close()
        soundThread?.interrupt()
        soundThread = null
        applicationSetting.soundStreamStatus.value = false
    }

    fun startSoundServer() {
        if (soundThread == null) {
            soundThread = Thread {
                server = ServerSocket(SERVICE_PORT + 1)
                client = server!!.accept()
                startSound()
                applicationSetting.soundStreamStatus.value = true
            }
            soundThread!!.start()
        } else {
            closeSocket()
            startSoundServer()
        }
    }

    fun restartSoundStream(onSuccess: () -> Unit) {
        CommendUtil.sendCommend(HttpCommend.CLOSE_SOUND, callback = {
            if (it) {
                closeSocket()
                startSoundStream()
                onSuccess()
            }
        })
    }

    private fun sendSystemSound(sendTarget: OutputStream): Thread {
        val info = DataLine.Info(TargetDataLine::class.java, audioFormat)
        val targetDataLine = getLine(info)
        targetDataLine.open(audioFormat)
        targetDataLine.start()
        val sendThread = Thread {
            try {
                while (targetDataLine.read(buffer, 0, bufSize).also { nByte = it } > 0) {
                    sendTarget.write(buffer, 0, nByte)
                }
            } catch (_: Exception) {}
        }
        sendThread.start()
        return sendThread

    }

    private fun playSoundStream(soundSource: InputStream): Thread {
        val dataLineInfo = DataLine.Info(SourceDataLine::class.java, audioFormat, AudioSystem.NOT_SPECIFIED)
        val sourceDataLine = AudioSystem.getLine(dataLineInfo) as SourceDataLine
        sourceDataLine.open(audioFormat)
        sourceDataLine.start()
        val playThread = Thread {
            try {
                while (soundSource.read(buffer, 0, bufSize).also { nByte = it } > 0) {
                    sourceDataLine.write(buffer, 0, nByte)
                }
            } catch (_: Exception){}
        }
        playThread.start()
        return playThread
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
