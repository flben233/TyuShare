package component.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import applicationSetting
import cn.hutool.core.io.FileUtil
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import component.material.SwitchWithTag
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.*
import java.awt.Desktop
import java.net.URI

@Preview
@Composable
fun SettingDialog(onCloseRequest: () -> Unit) {
    var showDirectoryPicker by remember { mutableStateOf(false) }
    DirectoryPicker(show = showDirectoryPicker) {
        if (it != null) {
            applicationSetting.fileReceivePath.value = it
        }
        showDirectoryPicker = false
    }
    Dialog(onDismissRequest = onCloseRequest) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxSize().padding(0.dp, 80.dp)
        ) {
            Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Settings, "")
                        Spacer(Modifier.width(5.dp))
                        Text("设置", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("系统设置", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 5.dp))
                    SwitchWithTag("开机启动", applicationSetting.launchWithSystem.value) {
                        setAutoLaunch(it)
                    }
                    SwitchWithTag("启动后显示程序窗口", applicationSetting.defaultOpenWindow.value) {
                        applicationSetting.defaultOpenWindow.value = it
                    }
                    Text("文件接收设置", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
                    Text("文件默认保存位置")
                    OutlinedButton(onClick = { showDirectoryPicker = true }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)) {
                        Spacer(Modifier.height(5.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(applicationSetting.fileReceivePath.value, fontSize = 15.sp)
                            Spacer(Modifier.width(10.dp))
                            Icon(painter = painterResource("/icons/more_horiz.svg"), "", Modifier.height(15.dp))
                        }
                    }
                    Text("音频设置", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
                    Button(onClick = { openBrowser() }) {
                        Text("下载VB-Cable音频驱动")
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onCloseRequest) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}

private fun setAutoLaunch(auto: Boolean) {
    if (auto) {
        for (file in FileUtil.loopFiles(System.getProperty("user.dir"))) {
            if (FileUtil.getSuffix(file).contains("exe")) {
                Runtime.getRuntime().exec("sc create TyuShare binPath= $file start= auto")
            }
        }
    } else {
        Runtime.getRuntime().exec("sc delete TyuShare")
    }
}

private fun openBrowser() {
    val uri = URI.create("https://download.vb-audio.com/Download_CABLE/VBCABLE_Driver_Pack43.zip")
    val dp = Desktop.getDesktop()
    if (dp.isSupported(Desktop.Action.BROWSE)) {
        dp.browse(uri)
    }
}
