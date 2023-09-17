package service

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import applicationSetting
import com.github.kwhat.jnativehook.GlobalScreen
import common.HttpCommend
import component.tool.KeyboardMode
import component.tool.showMask
import config.SERVICE_PORT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.KeyAction
import service.interfaces.BidirectionalService
import service.listener.GlobalKeyboardListener
import util.CommendUtil
import util.LoggerUtil
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import java.awt.event.InputEvent
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * 键鼠共享服务
 * @author ShirakawaTyu
 * @since 9/17/2023 5:30 PM
 * @version 1.0
 */
sealed class KeyboardShareService : BidirectionalService {
    // TODO: 改用TCP传输
    companion object Default : KeyboardShareService()

    // 按键最长按压时间
    private val keyTimeout: Long = 5000L

    private val servicePort = SERVICE_PORT + 5
    private val udpSocket: DatagramSocket = DatagramSocket(servicePort)
    private val bufSize = 4096
    private val robot: Robot = Robot()
    private var listenJob: Job? = null
    private val keyEventListener: GlobalKeyboardListener = GlobalKeyboardListener()
    var defaultPos: Point? = null

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
        } else {
            try {
                GlobalScreen.addNativeKeyListener(keyEventListener)
                showMask.value = true
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e)
            }
        }
    }

    override fun stop() {
        applicationSetting.keyboardShareStatus.value = false
        showMask.value = false
        try {
            GlobalScreen.removeNativeKeyListener(keyEventListener)
        } catch (_: Exception) {}
    }

    fun restart() {
        if (applicationSetting.keyboardShareStatus.value) {
            stop()
            start()
        }
    }

    /**
     * 发送鼠标动作
     * @param event 鼠标事件
     * @author ShirakawaTyu
     */
    @OptIn(ExperimentalComposeUiApi::class)
    fun sendMouse(event: PointerEvent) {
        val first = event.changes.first()
        val location = MouseInfo.getPointerInfo().location
        val offsetX = location.x - defaultPos!!.x
        val offsetY = location.y - defaultPos!!.y
        if ((offsetY != 0 && offsetX != 0) || (event.button != null) || (first.scrollDelta.y != 0f)) {
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

    /**
     * 发送鼠标或者键盘的动作
     * @param action 要发送的按键动作
     * @author ShirakawaTyu
     */
    fun sendKey(action: KeyAction) {
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

    private fun btnIndexToMask(pointerButton: PointerButton?): Int? {
        return when (pointerButton?.index) {
            PointerButton.Primary.index -> InputEvent.BUTTON1_DOWN_MASK
            PointerButton.Secondary.index -> InputEvent.BUTTON2_DOWN_MASK
            PointerButton.Tertiary.index -> InputEvent.BUTTON3_DOWN_MASK
            else -> null
        }
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
        CoroutineScope(Dispatchers.IO).launch {
            if (action.key != null && action.key != 0) {
                try {
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
                } catch (e: Exception) {
                    LoggerUtil.logStackTrace(e)
                    println(action.key)
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
        }
    }
}