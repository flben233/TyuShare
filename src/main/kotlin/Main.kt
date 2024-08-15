import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.dispatcher.VoidDispatchService
import component.dialog.SettingDialog
import component.material.TitleBar
import component.tray.ComposeTray
import component.tray.TrayItem
import model.ApplicationSetting
import service.listener.HotkeyListener
import service.transmission.HttpService
import service.transmission.UdpService
import util.JsonUtil


val SETTING_PATH = "${System.getProperty("user.home")}\\AppData\\Local\\TyuShare\\settings.json"
const val VERSION_CODE = 1.8
val currentView = mutableStateOf(Navigator.CONNECT_VIEW)
var applicationSetting = JsonUtil.parseJsonFile(SETTING_PATH, ApplicationSetting())
val isOpen = mutableStateOf(applicationSetting.defaultOpenWindow.value)
val tray = TrayState()
val hotkeyListener = HotkeyListener()

@Composable
@Preview
fun App(modifier: Modifier = Modifier) {
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
fun main() {
    if (System.getProperty("java.home") == null) {
        System.setProperty("java.home", ".")
    }
    application {
        LaunchedEffect(Unit) {
//            FlatLightLaf.setup()
            if (System.getProperty("compose.application.resources.dir") == null) {
                System.setProperty("compose.application.resources.dir", ".\\bin")
            }
            GlobalScreen.addNativeKeyListener(hotkeyListener)
            GlobalScreen.setEventDispatcher(VoidDispatchService())
            GlobalScreen.registerNativeHook()
            HttpService.startServer()
            UdpService.startServer()
        }

        val state = rememberWindowState(placement = WindowPlacement.Floating)
        var showMenu by remember { mutableStateOf(false) }

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
                        onMenuRequest = { showMenu = true },
                        onMinimizeRequest = { state.isMinimized = true }
                    )
                    App(Modifier.weight(16f))
                    if (showMenu) {
                        SettingDialog {
                            JsonUtil.toJsonFile(SETTING_PATH, applicationSetting)
                            showMenu = false
                        }
                    }
                }
            }
        }
        ComposeTray(
            icon = painterResource("favicon-64.png"),
            tooltip = "小雨妙享",
            onAction = { isOpen.value = true }
        ) {
            TrayItem("显示主界面") { isOpen.value = true }
            TrayItem("退出") {
                JsonUtil.toJsonFile(SETTING_PATH, applicationSetting)
                exitApplication()
            }
        }
    }
}