package service

import androidx.compose.ui.ExperimentalComposeUiApi
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
import util.RobotKeyAdapter
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import java.awt.Toolkit
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

/**
 * 键鼠共享服务
 * @author ShirakawaTyu
 * @since 9/17/2023 5:30 PM
 * @version 1.0
 */
sealed class KeyboardShareService : BidirectionalService {

    companion object Default : KeyboardShareService()

    private val servicePort = SERVICE_PORT + 5
    private val udpSocket: DatagramSocket = DatagramSocket(servicePort)
    private val bufSize = 4096
    private val robot: Robot = Robot()
    private var listenJob: Job? = null
    private val keyEventListener: GlobalKeyboardListener = GlobalKeyboardListener()
    private val toolkit = Toolkit.getDefaultToolkit()
    private val densityDpi = toolkit.screenResolution
    private val screenSize = toolkit.screenSize
    var defaultPos: Point? = null

    override fun sendCommendAndStop() {
        CommendUtil.sendCommend(HttpCommend.STOP_KEY_SHARE) {
            stop()
        }
    }

    override fun sendCommendAndStart() {
        val header = if (applicationSetting.keyboardMode.value == KeyboardMode.BE_CONTROLLER) "1"
                     else "0"
        CommendUtil.sendCommend(HttpCommend.START_KEY_SHARE, headers = mapOf("Mode" to header)) {
            if (it) {
                start()
            }
        }
    }

    fun start(mode: String) {
        applicationSetting.keyboardMode.value = if (mode == "1") KeyboardMode.CONTROLLER else KeyboardMode.BE_CONTROLLER
        start()
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
        } catch (_: Exception) {
        }
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
        val action = KeyAction(
            location.x / screenSize.getWidth(),
            location.y  / screenSize.getHeight(),
            RobotKeyAdapter.btnIndexToMask(event.button),
            first.scrollDelta.y.toInt(),
            first.pressed,
            null,
            false,
            densityDpi
        )
        sendKey(action)
    }

    fun sendKeyboard(rawKey: Int, keyPressed: Boolean) {
        val action = KeyAction(keyPressed = keyPressed, key = RobotKeyAdapter.getRobotKeyCode(rawKey), dpi = densityDpi)
        KeyboardShareService.sendKey(action)
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
                handleKeyboard(action)
            } else {
                handleMouse(action)
            }
        }
    }

    private fun handleMouse(action: KeyAction) {
        robot.mouseWheel(action.mouseScroll)
        robot.mouseMove((action.mouseX * screenSize.getWidth()).toInt(),
            (action.mouseY * screenSize.getHeight()).toInt()
        )
        action.mouseButton?.let {
            if (action.mousePressed) {
                robot.mousePress(it)
            } else {
                robot.mouseRelease(it)
            }
        }
    }

    private fun handleKeyboard(action: KeyAction) {
        try {
            action.key?.let {
                if (action.keyPressed) {
                    robot.keyPress(it)
                } else {
                    robot.keyRelease(it)
                }
            }
        } catch (e: Exception) {
            LoggerUtil.logStackTrace(e)
            println(action.key)
        }
    }

}