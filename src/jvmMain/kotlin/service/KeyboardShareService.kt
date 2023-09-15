package service

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import applicationSetting
import common.HttpCommend
import component.tool.KeyboardMode
import config.SERVICE_PORT
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.KeyAction
import service.interfaces.BidirectionalService
import util.CommendUtil
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.Point
import java.awt.event.MouseEvent
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.Timer
import java.util.TimerTask

sealed class KeyboardShareService : BidirectionalService {

    companion object Default : KeyboardShareService()

    private val servicePort = SERVICE_PORT + 5
    private val udpSocket: DatagramSocket = DatagramSocket(servicePort)
    private val bufSize = 4096
    private val robot: Robot = Robot()
    private var listenJob: Job? = null
    var defaultPos: Point? = null
    private val keyTimeout: Long = 5000L

    override fun sendCommendAndStop() {
        CommendUtil.sendCommend(HttpCommend.STOP_KEY_SHARE) {
            stop()
        }
    }

    override fun sendCommendAndStart() {
        CommendUtil.sendCommend(HttpCommend.START_KEY_SHARE) {
            if (it) {
                start()
            }
        }
    }

    override fun start() {
        applicationSetting.keyboardShareStatus.value = true
        KeyboardShareService.defaultPos = MouseInfo.getPointerInfo().location
        if (applicationSetting.keyboardMode.value == KeyboardMode.BE_CONTROLLER) {
            listenKeyFromUdp()
        }
    }

    override fun stop() {
        applicationSetting.keyboardShareStatus.value = false
    }

    fun restart() {
        stop()
        start()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun sendMouse(event: PointerEvent) {
        val first = event.changes.first()
        val location = MouseInfo.getPointerInfo().location
        val offsetX = location.x - defaultPos!!.x
        val offsetY = location.y - defaultPos!!.y
        println(first.position)
        if (offsetY != 0 && offsetX != 0) {
            robot.mouseMove(defaultPos!!.x, defaultPos!!.y)
            val action = KeyAction(
                offsetX,
                offsetY,
                btnIndexToMask(event.button),
                first.scrollDelta.y.toInt(),
                first.pressed,
                null,
                false
            )
            sendKey(action)
        }
    }

    fun sendKeyboard(event: KeyEvent) {
        val action = KeyAction(key = event.key.nativeKeyCode, keyPressed = (event.type == KeyEventType.KeyDown))
        sendKey(action)
    }

    private fun sendKey(action: KeyAction) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Json.encodeToString(action).toByteArray(StandardCharsets.UTF_8)
            val buffer = ByteArray(bufSize)
            for ((index, byte) in json.withIndex()) {
                buffer[index] = byte
            }
            udpSocket.send(
                DatagramPacket(
                    buffer,
                    json.size,
                    InetSocketAddress(ConnectionService.getTargetIp(), servicePort)
                )
            )
        }
    }

    private fun btnIndexToMask(index: PointerButton?): Int? {
        when (index) {
            PointerButton.Primary -> InputEvent.BUTTON1_DOWN_MASK
            PointerButton.Secondary -> InputEvent.BUTTON2_DOWN_MASK
            PointerButton.Tertiary -> InputEvent.BUTTON3_DOWN_MASK
        }
        return null
    }

    private fun listenKeyFromUdp() {
        listenJob = CoroutineScope(Dispatchers.IO).launch {
            val udpPacket = DatagramPacket(ByteArray(bufSize), bufSize)
            while (applicationSetting.keyboardShareStatus.value) {
                udpSocket.receive(udpPacket)
                val actionJson = String(
                    udpPacket.data.sliceArray(IntRange(0, udpPacket.length - 1)),
                    StandardCharsets.UTF_8
                ).trim()
                val keyAction = Json.decodeFromString<KeyAction>(actionJson)
                handleKey(keyAction)
            }
        }
    }

    private fun handleKey(action: KeyAction) {
        Thread {
            if (action.key != null && action.key != 0) {
                if (action.keyPressed) {
                    robot.keyPress(action.key!!)
                    Timer().schedule(object: TimerTask() {
                        override fun run() {
                            robot.keyRelease(action.key!!)
                        }
                    }, keyTimeout)
                } else {
                    robot.keyRelease(action.key!!)
                }
            } else {
                robot.mouseWheel(action.mouseScroll)
                val location = MouseInfo.getPointerInfo().location
                robot.mouseMove(location.x + action.mouseX, location.y + action.mouseY)
                action.mouseButton?.let {
                    if (action.mousePressed) {
                        robot.mousePress(it)
                        Timer().schedule(object: TimerTask() {
                            override fun run() {
                                robot.mouseRelease(it)
                            }
                        }, keyTimeout)
                    } else {
                        robot.mouseRelease(it)
                    }
                }
            }
        }.start()
    }
}