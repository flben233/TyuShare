package component.tool

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import applicationSetting
import component.material.MaterialCard
import service.KeyboardShareService
import java.awt.Toolkit

class KeyboardMode {
    companion object {
        var CONTROLLER = "控制端"
        var BE_CONTROLLER = "被控端"
    }
}
val showMask = mutableStateOf(false)


/**
 * 键鼠共享工具卡片
 * @author ShirakawaTyu
 * @since 9/17/2023 5:02 PM
 * @version 1.0
 */
@Composable
fun KeyboardShare(modifier: Modifier) {
    var expended by remember { mutableStateOf(false) }

    fun onClickItem(mode: String) {
        expended = false
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
            }
        }
    }

    MaterialCard(
        "键鼠共享",
        "操作将从控制端发送至被控端，使用F11+F12可快速切换状态",
        modifier = modifier
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

    // 当处于控制端时的屏幕遮罩
    val maskState = rememberWindowState(placement = WindowPlacement.Floating)
    Window(
        undecorated = true,
        onCloseRequest = { showMask.value = false },
        transparent = true,
        visible = showMask.value,
        state = maskState,
    ) {
        val dpi = Toolkit.getDefaultToolkit().screenResolution
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val dpiUnit = 160f
        val windowsDpi = 96f
        val pixelRatio = dpi / windowsDpi
        val perDpPixel = dpi / dpiUnit
        maskState.size = DpSize(
            width = (screenSize.width * pixelRatio / perDpPixel).dp,
            height = (screenSize.height * pixelRatio / perDpPixel).dp
        )
        maskState.position = WindowPosition(0.dp, 0.dp)
        Surface(
            modifier = Modifier.pointerInput(Unit, onMouseEvent).alpha(0.2f).fillMaxSize(),
            color = Color.Black
        ) {}
    }
}