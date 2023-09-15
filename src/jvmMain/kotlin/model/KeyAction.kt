package model

import kotlinx.serialization.Serializable

/**
 * 仅传输鼠标操作时key值为null，否则mouse开头的值为0
 * 原则上不允许二者同时取到有效值
 */
@Serializable
data class KeyAction (
    var mouseX: Int = 0,
    var mouseY: Int = 0,
    var mouseButton: Int? = null,
    var mouseScroll: Int = 0,
    var mousePressed: Boolean = false,
    var key: Int? = null,
    var keyPressed: Boolean = false
)