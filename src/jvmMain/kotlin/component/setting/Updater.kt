package component.setting

import VERSION_CODE
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.hutool.core.swing.DesktopUtil
import cn.hutool.http.HttpUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Updater() {
    val updateText = mutableStateOf("当前版本: $VERSION_CODE, 点击检查更新")
    val openBrowser = mutableStateOf(false)
    Text("检查更新", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
    OutlinedButton(onClick = {
        if (openBrowser.value) {
            DesktopUtil.browse("https://note.shirakawatyu.top/article/148")
        } else {
            updateText.value = "正在检查..."
            CoroutineScope(Dispatchers.IO).launch {
                if (checkUpdate()) {
                    updateText.value = "有更新, 点我打开下载页面"
                    openBrowser.value = true
                } else {
                    updateText.value = "已是最新版本"
                }
            }
        }
    }) {
        Text(updateText.value)
    }
}

private fun checkUpdate(): Boolean {
    val version = HttpUtil.get("https://tyushare.shirakawatyu.top/api/version")
    return version.toFloat() > VERSION_CODE
}