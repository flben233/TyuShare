package component.tool

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import applicationSetting
import component.material.MaterialCard
import kotlinx.coroutines.delay
import service.KeyboardShareService
import java.awt.MouseInfo
import java.awt.Robot

class KeyboardMode {
    companion object {
        var CONTROLLER = "控制端"
        var BE_CONTROLLER = "被控端"
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun KeyboardShare(modifier: Modifier) {
    var expended by remember { mutableStateOf(false) }

    fun onClickItem(mode: String) {
        applicationSetting.keyboardMode.value = mode
        KeyboardShareService.restart()
    }

    val onMouseEvent: suspend PointerInputScope.() -> Unit = {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (applicationSetting.keyboardShareStatus.value &&
                    applicationSetting.keyboardMode.value == KeyboardMode.CONTROLLER
                ) {
                    KeyboardShareService.sendMouse(event)
                }
                val first = event.changes.first()
                println(first.position)
                println(first.previousPosition)
                println(first.pressed)
                println(event.button)
                println(first.scrollDelta)
            }
        }
    }

    // TODO: 开启后显示一个大窗口防止鼠标跑出操作区域同时实现鼠标左右键
    // TODO: 阻止键盘操作

    MaterialCard(
        "键鼠共享",
        "鼠标和键盘的操作将从控制端发送至被控端，如要关闭请在被控端操作或使用Alt+Tab",
        modifier = modifier.pointerInput(Unit, onMouseEvent)
            .onKeyEvent {
                KeyboardShareService.sendKeyboard(it)
                true
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Switch(
                checked = applicationSetting.keyboardShareStatus.value,
                onCheckedChange = {
                if (it) {
                    KeyboardShareService.sendCommendAndStart()
                } else {
                    KeyboardShareService.sendCommendAndStop()
                }
            })
            Spacer(Modifier.padding(5.dp))
            TextButton(onClick = { expended = true }) {
                Text("当前模式: ${applicationSetting.keyboardMode.value}")
                DropdownMenu(expended, { expended = false }) {
                    DropdownMenuItem(onClick = { onClickItem(KeyboardMode.BE_CONTROLLER) },
                        text = { Text(fontWeight = FontWeight.Normal, text = KeyboardMode.BE_CONTROLLER) })
                    DropdownMenuItem(onClick = { onClickItem(KeyboardMode.CONTROLLER) },
                        text = { Text(fontWeight = FontWeight.Normal, text = KeyboardMode.CONTROLLER) })
                }
            }
        }
    }
}