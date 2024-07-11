package util

import com.github.kwhat.jnativehook.NativeInputEvent

sealed class NativeEventUtil {
    companion object Default: NativeEventUtil()

    fun consumeEvent(nativeInputEvent: NativeInputEvent?) {
        if (nativeInputEvent == null) {
            return
        }
        try {
            val field = NativeInputEvent::class.java.getDeclaredField("reserved")
            field.isAccessible = true
            field.setShort(nativeInputEvent, (0x01).toShort())
        } catch (e: Exception) {
            LoggerUtil.logStackTrace(e)
        }
    }
}