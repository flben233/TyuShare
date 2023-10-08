package component.setting

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import applicationSetting
import cn.hutool.core.io.FileUtil
import component.material.SwitchWithTag
import java.io.File
import java.io.FileWriter

@Composable
fun System() {
    Text("系统设置", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
    SwitchWithTag("开机启动 (以管理员权限设置此项，启动后获得管理员权限)", applicationSetting.launchWithSystem.value) {
        applicationSetting.launchWithSystem.value = it
        setAutoLaunch(it)
    }
    SwitchWithTag("启动后显示程序窗口", applicationSetting.defaultOpenWindow.value) {
        applicationSetting.defaultOpenWindow.value = it
    }
}

private fun setAutoLaunch(auto: Boolean) {
    val userHome = System.getProperty("user.home")
    val link = "$userHome\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\TyuShare.bat"
    val userPath = System.getProperty("user.dir")
    if (auto) {
        for (file in FileUtil.loopFiles(userPath)) {
            if (FileUtil.getSuffix(file).contains("exe")) {
                val file1 = File(link)
                if (!file1.exists()) {
                    file1.createNewFile()
                }
                val fileWriter = FileWriter(file1)
                fileWriter.write(userPath.substring(0, 2) + "\r\n")
                fileWriter.write("cd $userPath\r\n")
                fileWriter.write("start \"\" \"$file\"")
                fileWriter.close()
            }
        }
    } else {
        File(link).delete()
    }
}
