package service.listener

import com.github.kwhat.jnativehook.NativeInputEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import model.KeyAction
import service.KeyboardShareService
import util.LoggerUtil
import util.RobotKeyAdapter


/**
 * 全局键盘监听器，注意这个监听器会阻止事件继续传递，即阻止键盘操作
 * @author ShirakawaTyu
 * @since 9/17/2023 5:08 PM
 * @version 1.0
 */
class GlobalKeyboardListener: NativeKeyListener {

    override fun nativeKeyPressed(nativeEvent: NativeKeyEvent?) {
        if (nativeEvent != null) {
            try {
                val field = NativeInputEvent::class.java.getDeclaredField("reserved")
                field.isAccessible = true
                field.setShort(nativeEvent, (0x01).toShort())
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e)
            }
            val action = KeyAction(keyPressed = true, key = RobotKeyAdapter.getRobotKeyCode(nativeEvent.keyCode))
            KeyboardShareService.sendKey(action)
        }
    }

    override fun nativeKeyReleased(nativeEvent: NativeKeyEvent?) {
        if (nativeEvent != null) {
            try {
                val field = NativeInputEvent::class.java.getDeclaredField("reserved")
                field.isAccessible = true
                field.setShort(nativeEvent, (0x01).toShort())
            } catch (e: Exception) {
                LoggerUtil.logStackTrace(e)
            }
            val action = KeyAction(keyPressed = false, key = RobotKeyAdapter.getRobotKeyCode(nativeEvent.keyCode))
            KeyboardShareService.sendKey(action)
        }
    }
}