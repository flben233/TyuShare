package view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import component.info.StatusPanel
import component.tool.ClipboardShare
import component.tool.FileTransfer
import component.tool.KeyboardShare
import component.tool.SoundStream


@Preview
@Composable
fun MainView() {
    Row (Modifier.fillMaxSize()) {
        Column(
            Modifier.padding(PaddingValues(top = 15.dp)).fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.Center) {
            StatusPanel()
        }
        Spacer(Modifier.width(15.dp))
        Column (Modifier.fillMaxHeight().weight(1f)) {
            SoundStream(modifier = Modifier.weight(1f))
            KeyboardShare(modifier = Modifier.weight(1f))
            Row (modifier = Modifier.weight(1f)) {
                ClipboardShare(Modifier.weight(1f))
                Spacer(Modifier.width(15.dp))
                FileTransfer(Modifier.weight(1f))
            }
        }
    }
}