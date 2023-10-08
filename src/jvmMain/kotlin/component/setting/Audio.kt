package component.setting

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.hutool.core.swing.DesktopUtil

@Composable
fun Audio() {
    Text("音频设置", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
    Button(onClick = { DesktopUtil.browse("https://download.vb-audio.com/Download_CABLE/VBCABLE_Driver_Pack43.zip") }) {
        Text("下载VB-Cable音频驱动")
    }

}