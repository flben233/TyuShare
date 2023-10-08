package component.setting

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.hutool.core.swing.DesktopUtil


@Composable
fun Github() {
    Text("项目地址", fontWeight = FontWeight.Bold, modifier = Modifier.padding(0.dp, 10.dp))
    OutlinedButton(
        onClick = { DesktopUtil.browse("https://github.com/flben233/TyuShare") },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
    ) {
        Spacer(Modifier.height(5.dp))
        Text("https://github.com/flben233/TyuShare", fontSize = 15.sp)
    }
}