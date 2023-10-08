package component.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import applicationSetting
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker

@Composable
fun FileReceive() {
    var showDirectoryPicker by remember { mutableStateOf(false) }
    DirectoryPicker(show = showDirectoryPicker) {
        if (it != null) {
            applicationSetting.fileReceivePath.value = it
        }
        showDirectoryPicker = false
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
}