package service.listener

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import service.KeyboardShareService
import util.NativeEventUtil


/**
 * 全局键盘监听器，注意这个监听器会阻止事件继续传递，即阻止键盘操作
 * @author ShirakawaTyu
 * @since 9/17/2023 5:08 PM
 * @version 1.0
 */
class GlobalKeyboardListener: NativeKeyListener {

    override fun nativeKeyPressed(nativeEvent: NativeKeyEvent?) {
        handleKey(nativeEvent, true)
    }

    override fun nativeKeyReleased(nativeEvent: NativeKeyEvent?) {
        handleKey(nativeEvent, false)
    }

    private fun handleKey(nativeEvent: NativeKeyEvent?, pressed: Boolean) {
        if (nativeEvent != null) {
            NativeEventUtil.consumeEvent(nativeEvent)
            KeyboardShareService.sendKeyboard(nativeEvent.keyCode, pressed)
        }
    }
}