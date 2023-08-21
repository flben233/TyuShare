package component.tool

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import applicationSetting
import cn.hutool.core.io.StreamProgress
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import component.material.MaterialCard
import service.FileTransferService

var progress = mutableStateOf(0.01f)
var showDialog = mutableStateOf(false)
var totalMByte = mutableStateOf("0.00")
var progressMByte = mutableStateOf("0.00")

class FileStreamProgress: StreamProgress {
    override fun start() {
        showDialog.value = true
    }

    override fun progress(total: Long, progressSize: Long) {
        totalMByte.value = String.format("%.2f MB", total / (1024 * 1024.0))
        progressMByte.value = String.format("%.2f MB", progressSize / (1024 * 1024.0))
        progress.value = progressSize / total.toFloat()
    }

    override fun finish() {
        showDialog.value = false
    }

}

@Composable
fun FileTransfer(modifier: Modifier = Modifier) {
    var showFilePicker by remember { mutableStateOf(false) }
    FilePicker(show = showFilePicker) {
        if (it != null) {
            FileTransferService.sendFile(it.path)
        }
        showFilePicker = false
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(text = "正在传输文件") },
            text = {
                LinearProgressIndicator(progress = progress.value, Modifier.fillMaxWidth())
                Spacer(Modifier.height(5.dp))
                Text("Tips: 接收到的文件放在${applicationSetting.fileReceivePath.value}")
                   },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("完成")
                }
            },
        )
    }
    MaterialCard(title = "文件传输", description = "可以让你从这台电脑传文件到另一台电脑", modifier = modifier) {
        Column(Modifier.fillMaxWidth()) {
            Spacer(Modifier.height(5.dp))
            Button(onClick = { showFilePicker = true }) {
                Text("选择文件")
            }
        }
    }
}