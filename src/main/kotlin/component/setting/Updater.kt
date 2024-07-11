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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import util.AwtUtil
import util.HttpUtil

@Composable
fun Updater() {
    val updateText = mutableStateOf("当前版本: $VERSION_CODE, 点击检查更新")
    var openBrowser = false
    fun check() {
        if (openBrowser) {
            AwtUtil.browse("https://note.shirakawatyu.top/article/148")
        } else {
            updateText.value = "正在检查..."
            CoroutineScope(Dispatchers.IO).launch {
                if (checkUpdate()) {
                    updateText.value = "有更新, 点我打开下载页面"
                    openBrowser = true
                } else {
                    updateText.value = "当前版本: $VERSION_CODE, 已是最新版本"
                }
            }
        }
    }
    check()
    Text("检查更新", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
    OutlinedButton(onClick = {
        check()
    }) {
        Text(updateText.value)
    }
}

private fun checkUpdate(): Boolean {
    try {
        val apiJson = HttpUtil.get("https://tyushare.shirakawatyu.top").body()
        val jsonObj = Json.parseToJsonElement(apiJson).jsonObject
        val version = jsonObj["tag_name"].toString().replace("\"", "").toDouble()
        println(version)
        return version > VERSION_CODE
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}