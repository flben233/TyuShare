import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.dispatcher.VoidDispatchService
import component.dialog.SettingDialog
import component.material.TitleBar
import model.ApplicationSetting
import service.ConnectionService
import service.listener.HotkeyListener
import util.JsonUtil


val SETTING_PATH = "${System.getProperty("user.home")}\\AppData\\Local\\TyuShare\\settings.json"
const val VERSION_CODE = 1.3
val currentView = mutableStateOf(Navigator.CONNECT_VIEW)
var applicationSetting = JsonUtil.parseJsonFile(SETTING_PATH, ApplicationSetting())
val isOpen = mutableStateOf(applicationSetting.defaultOpenWindow.value)
val tray = TrayState()
val hotkeyListener = HotkeyListener()
var added = false

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



// TODO: 文件拖拽发送
// TODO: 当本机为主机时自动切换对方角色
// TODO: 音频输出自动切换
fun main() = application {
    FlatLightLaf.setup()
    GlobalScreen.setEventDispatcher(VoidDispatchService())
    GlobalScreen.registerNativeHook()
    if (!added) {
        GlobalScreen.addNativeKeyListener(hotkeyListener)
        added = true
    }
    val state = rememberWindowState(placement = WindowPlacement.Floating)
    val showMenu = remember { mutableStateOf(false) }

    Window(
        undecorated = true,
        onCloseRequest = {
            JsonUtil.toJsonFile(SETTING_PATH, applicationSetting)
            isOpen.value = false
        },
        title = "小雨妙享",
        icon = painterResource("favicon-64.png"),
        visible = isOpen.value,
        state = state,
        transparent = true
    ) {
        Surface(
            modifier = Modifier.padding(10.dp),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(5.dp),
            shadowElevation = 3.dp
        ) {
            Column {
                TitleBar(
                    modifier = Modifier.weight(1f),
                    onCloseRequest = {
                        JsonUtil.toJsonFile(SETTING_PATH, applicationSetting)
                        isOpen.value = false
                    },
                    onMenuRequest = { showMenu.value = true },
                    onMinimizeRequest = { state.isMinimized = true }
                )
                App(Modifier.weight(16f))
                if (showMenu.value) {
                    SettingDialog {
                        JsonUtil.toJsonFile(SETTING_PATH, applicationSetting)
                        showMenu.value = false
                    }
                }
            }
        }
    }

    val trayState = remember { tray }
    Tray(icon = painterResource("favicon-64.png"), state = trayState, onAction = { isOpen.value = true }, menu = {
        Item("显示主界面", onClick = { isOpen.value = true })
        Item("退出", onClick = {
            JsonUtil.toJsonFile(SETTING_PATH, applicationSetting)
            exitApplication()
        })
    })
}




