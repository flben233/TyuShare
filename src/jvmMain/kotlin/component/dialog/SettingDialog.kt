package component.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.io.File
import java.io.FileWriter
import java.net.URI

private const val ANIMATION_TIME = 180

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

    val visibility = remember { mutableStateOf(false) }
    LaunchedEffect("setting") {
        visibility.value = true
    }


    Dialog(onDismissRequest = {
        CoroutineScope(Dispatchers.Default).launch {
            exitWithAnimation(visibility) {
                onCloseRequest()
            }
        }
    }) {
        AnimatedVisibility(
            visible = visibility.value,
            enter = fadeIn(animationSpec = tween(ANIMATION_TIME)),
            exit = fadeOut(
                animationSpec = tween(
                    ANIMATION_TIME
                )
            )
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxSize().padding(0.dp, 40.dp)
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
                            applicationSetting.launchWithSystem.value = it
                            setAutoLaunch(it)
                        }
                        SwitchWithTag("启动后显示程序窗口", applicationSetting.defaultOpenWindow.value) {
                            applicationSetting.defaultOpenWindow.value = it
                        }
                        Text("文件接收设置", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
                        Text("文件默认保存位置")
                        OutlinedButton(
                            onClick = { showDirectoryPicker = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                        ) {
                            Spacer(Modifier.height(5.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(applicationSetting.fileReceivePath.value, fontSize = 15.sp)
                                Spacer(Modifier.width(10.dp))
                                Icon(painter = painterResource("/icons/more_horiz.svg"), "", Modifier.height(15.dp))
                            }
                        }
                        Text("音频设置", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
                        Button(onClick = { openBrowser("https://download.vb-audio.com/Download_CABLE/VBCABLE_Driver_Pack43.zip") }) {
                            Text("下载VB-Cable音频驱动")
                        }
                        Text("项目地址", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
                        OutlinedButton(
                            onClick = { openBrowser("https://github.com/flben233/TyuShare") },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                        ) {
                            Spacer(Modifier.height(5.dp))
                            Text("https://github.com/flben233/TyuShare", fontSize = 15.sp)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = {
                            CoroutineScope(Dispatchers.Default).launch {
                                exitWithAnimation(visibility) {
                                    onCloseRequest()
                                }
                            }
                        }) {
                            Text("关闭")
                        }
                    }
                }
            }
        }
    }
}

private fun setAutoLaunch(auto: Boolean) {
    val userHome = System.getProperty("user.home")
    val link = "$userHome\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\TyuShare.bat"
    val userpath = System.getProperty("user.dir")
    if (auto) {
        for (file in FileUtil.loopFiles(userpath)) {
            if (FileUtil.getSuffix(file).contains("exe")) {
                val file1 = File(link)
                if (!file1.exists()) {
                    file1.createNewFile()
                }
                val fileWriter = FileWriter(file1)
                fileWriter.write(userpath.substring(0, 2) + "\r\n")
                fileWriter.write("cd $userpath\r\n")
                fileWriter.write("start \"\" \"$file\"")
                fileWriter.close()
            }
        }
    } else {
        File(link).delete()
    }
}

private fun openBrowser(url: String) {
    val uri = URI.create(url)
    val dp = Desktop.getDesktop()
    if (dp.isSupported(Desktop.Action.BROWSE)) {
        dp.browse(uri)
    }
}

suspend fun exitWithAnimation(
    animateTrigger: MutableState<Boolean>,
    onDismissRequest: () -> Unit
) {
    animateTrigger.value = false
    delay(ANIMATION_TIME.toLong())
    onDismissRequest()
}
