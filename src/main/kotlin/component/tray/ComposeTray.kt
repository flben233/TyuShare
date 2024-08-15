package component.tray

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

val layout: Class<*> = Class.forName("androidx.compose.ui.window.LayoutConfiguration_desktopKt")
val globalDensity = layout.getMethod("getGlobalDensity").invoke(null) as Density
val globalLayoutDirection = layout.getMethod("getGlobalLayoutDirection").invoke(null) as LayoutDirection
val showMenu = mutableStateOf(false)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposeTray(
    icon: Painter,
    tooltip: String? = null,
    onAction: () -> Unit = {},
    menu: @Composable () -> Unit = {}
) {
    val currentOnAction by rememberUpdatedState(onAction)
    val awtIcon = remember(icon) {
        icon.toAwtImage(globalDensity, globalLayoutDirection, Size(16f, 16f))
    }
    val popupState = rememberDialogState()
    val tray = remember {
        TrayIcon(awtIcon).apply {
            isImageAutoSize = true

            addActionListener {
                currentOnAction()
            }
        }
    }

    SideEffect {
        if (tray.image != awtIcon) tray.image = awtIcon
        if (tray.toolTip != tooltip) tray.toolTip = tooltip
    }
    DisposableEffect(Unit) {
        tray.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                showPopup(e)
            }

            override fun mousePressed(e: MouseEvent) {
                showPopup(e)
            }

            private fun showPopup(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    popupState.position = WindowPosition(pxToDp(e.x), pxToDp(e.y) - 64.dp)
                    showMenu.value = true
                }
            }
        })
        SystemTray.getSystemTray().add(tray)
        onDispose {
            SystemTray.getSystemTray().remove(tray)
        }
    }


    DialogWindow(
        undecorated = true,
        onCloseRequest = { showMenu.value = false },
        visible = showMenu.value,
        transparent = true,
        alwaysOnTop = true,
        resizable = false,
        state = popupState
    ) {
        Surface(
            shadowElevation = 3.dp,
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.width(100.dp).onPointerEvent(PointerEventType.Exit) {
                showMenu.value = false
            }
        ) {
            Column {
                menu()
            }
        }
    }
}

private fun pxToDp(pxValue: Int): Dp {
    return (pxValue / globalDensity.density).dp
}