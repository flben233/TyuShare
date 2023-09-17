package util

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*
import java.awt.event.KeyEvent

sealed class RobotKeyAdapter {
    companion object Default: RobotKeyAdapter()
    fun getRobotKeyCode(keyCode: Int): Int {
        return when (keyCode) {
            VC_ESCAPE -> KeyEvent.VK_ESCAPE

            // Begin Function Keys
            VC_F1 -> KeyEvent.VK_F1
            VC_F2 -> KeyEvent.VK_F2
            VC_F3 -> KeyEvent.VK_F3
            VC_F4 -> KeyEvent.VK_F4
            VC_F5 -> KeyEvent.VK_F5
            VC_F6 -> KeyEvent.VK_F6
            VC_F7 -> KeyEvent.VK_F7
            VC_F8 -> KeyEvent.VK_F8
            VC_F9 -> KeyEvent.VK_F9
            VC_F10 -> KeyEvent.VK_F10
            VC_F11 -> KeyEvent.VK_F11
            VC_F12 -> KeyEvent.VK_F12
            VC_F13 -> KeyEvent.VK_F13
            VC_F14 -> KeyEvent.VK_F14
            VC_F15 -> KeyEvent.VK_F15
            VC_F16 -> KeyEvent.VK_F16
            VC_F17 -> KeyEvent.VK_F17
            VC_F18 -> KeyEvent.VK_F18
            VC_F19 -> KeyEvent.VK_F19
            VC_F20 -> KeyEvent.VK_F20
            VC_F21 -> KeyEvent.VK_F21
            VC_F22 -> KeyEvent.VK_F22
            VC_F23 -> KeyEvent.VK_F23
            VC_F24 -> KeyEvent.VK_F24
            // End Function Keys

            // Begin Alphanumeric Zone
            VC_BACKQUOTE -> KeyEvent.VK_BACK_QUOTE
            VC_1 -> KeyEvent.VK_1
            VC_2 -> KeyEvent.VK_2
            VC_3 -> KeyEvent.VK_3
            VC_4 -> KeyEvent.VK_4
            VC_5 -> KeyEvent.VK_5
            VC_6 -> KeyEvent.VK_6
            VC_7 -> KeyEvent.VK_7
            VC_8 -> KeyEvent.VK_8
            VC_9 -> KeyEvent.VK_9
            VC_0 -> KeyEvent.VK_0
            VC_MINUS -> KeyEvent.VK_MINUS
            VC_EQUALS -> KeyEvent.VK_EQUALS
            VC_BACKSPACE -> KeyEvent.VK_BACK_SPACE
            VC_TAB -> KeyEvent.VK_TAB
            VC_CAPS_LOCK -> KeyEvent.VK_CAPS_LOCK
            VC_A -> KeyEvent.VK_A
            VC_B -> KeyEvent.VK_B
            VC_C -> KeyEvent.VK_C
            VC_D -> KeyEvent.VK_D
            VC_E -> KeyEvent.VK_E
            VC_F -> KeyEvent.VK_F
            VC_G -> KeyEvent.VK_G
            VC_H -> KeyEvent.VK_H
            VC_I -> KeyEvent.VK_I
            VC_J -> KeyEvent.VK_J
            VC_K -> KeyEvent.VK_K
            VC_L -> KeyEvent.VK_L
            VC_M -> KeyEvent.VK_M
            VC_N -> KeyEvent.VK_N
            VC_O -> KeyEvent.VK_O
            VC_P -> KeyEvent.VK_P
            VC_Q -> KeyEvent.VK_Q
            VC_R -> KeyEvent.VK_R
            VC_S -> KeyEvent.VK_S
            VC_T -> KeyEvent.VK_T
            VC_U -> KeyEvent.VK_U
            VC_V -> KeyEvent.VK_V
            VC_W -> KeyEvent.VK_W
            VC_X -> KeyEvent.VK_X
            VC_Y -> KeyEvent.VK_Y
            VC_Z -> KeyEvent.VK_Z
            VC_OPEN_BRACKET -> KeyEvent.VK_OPEN_BRACKET
            VC_CLOSE_BRACKET -> KeyEvent.VK_CLOSE_BRACKET
            VC_BACK_SLASH -> KeyEvent.VK_BACK_SLASH
            VC_SEMICOLON -> KeyEvent.VK_SEMICOLON
            VC_QUOTE -> KeyEvent.VK_QUOTE
            VC_ENTER -> KeyEvent.VK_ENTER
            VC_COMMA -> KeyEvent.VK_COMMA
            VC_PERIOD -> KeyEvent.VK_PERIOD
            VC_SLASH -> KeyEvent.VK_SLASH
            VC_SPACE -> KeyEvent.VK_SPACE
            // End Alphanumeric Zone

            VC_PRINTSCREEN -> KeyEvent.VK_PRINTSCREEN
            VC_SCROLL_LOCK -> KeyEvent.VK_SCROLL_LOCK
            VC_PAUSE -> KeyEvent.VK_PAUSE
            VC_META -> KeyEvent.VK_META

            // Begin Edit Key Zone
            VC_INSERT -> KeyEvent.VK_INSERT
            VC_DELETE -> KeyEvent.VK_DELETE
            VC_HOME -> KeyEvent.VK_HOME
            VC_END -> KeyEvent.VK_END
            VC_PAGE_UP -> KeyEvent.VK_PAGE_UP
            VC_PAGE_DOWN -> KeyEvent.VK_PAGE_DOWN
            // End Edit Key Zone

            // Begin Cursor Key Zone
            VC_UP -> KeyEvent.VK_UP
            VC_LEFT -> KeyEvent.VK_LEFT
            VC_CLEAR -> KeyEvent.VK_CLEAR
            VC_RIGHT -> KeyEvent.VK_RIGHT
            VC_DOWN -> KeyEvent.VK_DOWN
            // End Cursor Key Zone

            // Begin Numeric Zone
            VC_NUM_LOCK -> KeyEvent.VK_NUM_LOCK
            VC_SEPARATOR -> KeyEvent.VK_SEPARATOR
            // End Numeric Zone

            // Begin Modifier and Control Keys
            VC_SHIFT -> KeyEvent.VK_SHIFT
            VC_CONTROL -> KeyEvent.VK_CONTROL
            VC_ALT -> KeyEvent.VK_ALT

            VC_CONTEXT_MENU -> KeyEvent.VK_CONTEXT_MENU
            // End Modifier and Control Keys

            VC_UNDEFINED -> KeyEvent.VK_UNDEFINED

            else -> KeyEvent.VK_UNDEFINED
        }
    }
}