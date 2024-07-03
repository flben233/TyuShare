package service.listener

import applicationSetting
import cn.hutool.core.collection.ConcurrentHashSet
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import service.KeyboardShareService


/**
 * 热键监听器，不会阻止事件传播，这个监听器目前只监听 F11+F12 这个操作
 * @author ShirakawaTyu
 * @since 9/17/2023 5:09 PM
 * @version 1.0
 */
class HotkeyListener: NativeKeyListener {
    private val keyCache: ConcurrentHashSet<Int> = ConcurrentHashSet()
    private val key1 = NativeKeyEvent.VC_CONTROL
    private val key2 = NativeKeyEvent.VC_F10
    override fun nativeKeyPressed(nativeEvent: NativeKeyEvent?) {
        keyCache.add(nativeEvent?.keyCode)
        if (keyCache.contains(key1) && keyCache.contains(key2)) {
            keyCache.clear()
            if (applicationSetting.keyboardShareStatus.value) {
                KeyboardShareService.sendCommendAndStop()
                KeyboardShareService.stop()
            } else {
                KeyboardShareService.sendCommendAndStart()
            }
        }
        if (nativeEvent?.keyCode != key1 && nativeEvent?.keyCode != key2) {
            keyCache.clear()
        }
    }

    override fun nativeKeyReleased(nativeEvent: NativeKeyEvent?) {
        keyCache.remove(nativeEvent?.keyCode)
    }
}