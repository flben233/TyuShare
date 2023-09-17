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

/**
 * 剪贴板共享工具卡片
 * @author ShirakawaTyu
 * @since 9/17/2023 5:01 PM
 * @version 1.0
 */
@Composable
fun ClipboardShare(modifier: Modifier = Modifier) {
    if (applicationSetting.clipboardStatus.value) {
        ClipboardShareService.sendCommendAndStart()
    }

    MaterialCard("剪贴板共享", "传文本", modifier = modifier) {
        Row (modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                Switch(applicationSetting.clipboardStatus.value, {
                    applicationSetting.clipboardStatus.value = it
                    if (it) {
                        ClipboardShareService.sendCommendAndStart()
                    } else {
                        ClipboardShareService.sendCommendAndStop()
                    }
                })
            }
        }
    }
}
