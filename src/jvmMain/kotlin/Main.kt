import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.formdev.flatlaf.FlatLightLaf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.ApplicationSetting
import service.ConnectionService
import util.JsonUtil


val currentView = mutableStateOf(Navigator.CONNECT_VIEW)
var applicationSetting = JsonUtil.parseJsonFile("./settings.json", ApplicationSetting())

@Composable
@Preview
fun App() {
    ConnectionService.startCommendServer()
    MaterialTheme {
        Column(Modifier.background(MaterialTheme.colorScheme.background).padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Settings, "")
                Spacer(Modifier.padding(3.dp))
                Text("小雨妙享", fontWeight = FontWeight.Bold)
            }
            navigateTo(currentView.value)
        }
    }
}
// TODO: 设置界面，包括开机启动、文件默认存储地址、启动时最小化到托盘
fun main() = application {
    FlatLightLaf.setup()
    val isOpen = remember { mutableStateOf(applicationSetting.defaultOpenWindow) }
    applicationSetting.fileReceivePath = ""
    Window(onCloseRequest = {
        JsonUtil.toJsonFile("./settings.json", applicationSetting)
        isOpen.value = false
    }, title = "", visible = isOpen.value) {
        App()
    }

    Tray(icon = ColorPainter(Color.Yellow), onAction = { isOpen.value = true }, menu = {
        Item("显示主界面", onClick = { isOpen.value = true })
        Item("退出", onClick = {
            JsonUtil.toJsonFile("./settings.json", applicationSetting)
            exitApplication()
        })
    })
}
