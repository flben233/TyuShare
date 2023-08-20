package component.tool

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import applicationSetting
import component.material.MaterialCard
import service.SoundStreamService


class SoundStreamMode {
    companion object {
        const val LISTENER = "聆听者"
        const val SPEAKER = "讲述人"
    }
}

@Composable
fun SoundStream(modifier: Modifier = Modifier) {
    var expended by remember { mutableStateOf(false) }
    fun onClickItem(text: String) {
        expended = false
        applicationSetting.soundStreamMode.value = text
        if (applicationSetting.soundStreamStatus.value) {
            SoundStreamService.restartSoundStream(applicationSetting.soundStreamMode.value) { applicationSetting.soundStreamStatus.value = true }
        }
    }

    MaterialCard("音频串流", "这个功能可以使你在这台电脑上听到另一台电脑的声音，也可以反过来", modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(applicationSetting.soundStreamStatus.value, {
                    applicationSetting.soundStreamStatus.value = it
                    if (it) {
                        SoundStreamService.startSoundStream(applicationSetting.soundStreamMode.value)
                    } else {
                        SoundStreamService.stopSoundStream()
                    }
                })
            }
            Spacer(Modifier.padding(5.dp))
            TextButton(onClick = { expended = true }) {
                Text("当前模式: ${applicationSetting.soundStreamMode.value}")
                DropdownMenu(expended, { expended = false }) {
                    DropdownMenuItem(onClick = { onClickItem(SoundStreamMode.LISTENER) },
                        text = { Text(fontWeight = FontWeight.Normal, text = SoundStreamMode.LISTENER) })
                    DropdownMenuItem(onClick = { onClickItem(SoundStreamMode.SPEAKER) },
                        text = { Text(fontWeight = FontWeight.Normal, text = SoundStreamMode.SPEAKER) })
                }
            }
        }
    }
}