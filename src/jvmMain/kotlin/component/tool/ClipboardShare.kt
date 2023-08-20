package component.tool

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import applicationSetting
import component.material.MaterialCard
import service.ClipboardShareService


@Composable
fun ClipboardShare(modifier: Modifier = Modifier) {
    ClipboardShareService.startShare()
    MaterialCard("剪贴板共享", "两台电脑共享剪贴板，十分令人快乐的功能", modifier = modifier) {
        Row (modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                Switch(applicationSetting.clipboardStatus.value, {
                    applicationSetting.clipboardStatus.value = it
                    if (it) {
                        ClipboardShareService.startShare()
                    } else {
                        ClipboardShareService.stopShare()
                    }
                })
            }
        }
    }
}