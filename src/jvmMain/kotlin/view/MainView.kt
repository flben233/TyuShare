package view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import component.info.StatusPanel
import component.tool.ClipboardShare
import component.tool.FileTransfer
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
//        LazyColumn (Modifier.fillMaxHeight().weight(1f)) {
//            item { SoundStream() }
//            item { ClipboardShare() }
//            item { FileTransfer() }
//        }
        Column (Modifier.fillMaxHeight().weight(1f)) {
            SoundStream(modifier = Modifier.weight(1f))
            ClipboardShare(modifier = Modifier.weight(1f))
            FileTransfer(modifier = Modifier.weight(1f))
        }
    }
}