package model

import kotlinx.serialization.Serializable

/**
 * 键鼠动作模型，表示键盘操作时就设置key相关属性，表示鼠标操作时就设置鼠标相关属性
 * 原则上不允许二者同时取到有效值
 * @author ShirakawaTyu
 * @since 9/17/2023 5:04 PM
 * @version 1.0
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
) {
    override fun toString(): String {
        return "KeyAction(mouseX=$mouseX, mouseY=$mouseY, mouseButton=$mouseButton, mouseScroll=$mouseScroll, mousePressed=$mousePressed, key=$key, keyPressed=$keyPressed)"
    }
}