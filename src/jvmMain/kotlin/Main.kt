import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.formdev.flatlaf.FlatLightLaf
import component.dialog.SettingDialog
import component.material.TitleBar
import model.ApplicationSetting
import service.ConnectionService
import util.JsonUtil


val currentView = mutableStateOf(Navigator.CONNECT_VIEW)
var applicationSetting = JsonUtil.parseJsonFile("./settings.json", ApplicationSetting())
val tray = TrayState()

@Composable
@Preview
fun App(modifier: Modifier = Modifier) {
    ConnectionService.startCommendServer()
    MaterialTheme {
        Column(modifier.background(MaterialTheme.colorScheme.background).padding(15.dp, 0.dp, 15.dp, 15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource("favicon-64.png"), "", Modifier.height(30.dp))
                Spacer(Modifier.width(5.dp))
                Text("小雨妙享", fontWeight = FontWeight.Bold)
            }
            navigateTo(currentView.value)
        }
    }
}

// TODO: 音频共享还没完善, 代码要优化一下
// TODO: WinAPI，音频硬件更改
// TODO: 过渡动画
fun main() = application {
    FlatLightLaf.setup()
    val isOpen = remember { mutableStateOf(applicationSetting.defaultOpenWindow.value) }
    val state = rememberWindowState(placement = WindowPlacement.Floating)
    val showMenu = remember { mutableStateOf(false) }


    Window(
        undecorated = true,
        onCloseRequest = {
            JsonUtil.toJsonFile("./settings.json", applicationSetting)
            isOpen.value = false
        },
        title = "小雨妙享",
        icon = painterResource("favicon-64.png"),
        visible = isOpen.value,
        state = state
    ) {
        Column {
            TitleBar(
                modifier = Modifier.weight(1f),
                onCloseRequest = {
                    JsonUtil.toJsonFile("./settings.json", applicationSetting)
                    isOpen.value = false
                },
                onMenuRequest = { showMenu.value = true },
                onMinimizeRequest = { state.isMinimized = true }
            )
            App(Modifier.weight(16f))
            if (showMenu.value) {
                SettingDialog {
                    JsonUtil.toJsonFile("./settings.json", applicationSetting)
                    showMenu.value = false
                }
            }
        }
    }

    val trayState = remember { tray }
    Tray(icon = painterResource("favicon-64.png"), state = trayState, onAction = { isOpen.value = true }, menu = {
        Item("显示主界面", onClick = { isOpen.value = true })
        Item("退出", onClick = {
            JsonUtil.toJsonFile("./settings.json", applicationSetting)
            exitApplication()
        })
    })
}
