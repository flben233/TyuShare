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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import applicationSetting
import component.material.MaterialCard
import service.KeyboardShareService

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

    fun onMouseEvent() {
        if (applicationSetting.keyboardShareStatus.value) {
            KeyboardShareService.sendMouse()
        }
    }

    MaterialCard("键鼠共享", "打开后你的鼠标和键盘的操作将从控制端发送至被控端，如要关闭请在被控端操作", modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val first = event.changes.first()
                        println(first.position)
                        println(first.previousPosition)
                        println(first.pressed)
                        println(event.button)
                        println(first.scrollDelta)
                    }
                }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(applicationSetting.keyboardShareStatus.value, {
                    applicationSetting.keyboardShareStatus.value = it
                })
            }
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