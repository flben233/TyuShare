package component.material

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

/**
 * 带标签的Switch组件
 * @author ShirakawaTyu
 * @since 9/17/2023 4:59 PM
 * @version 1.0
 */
@Composable
fun SwitchWithTag(tag: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = checked, onCheckedChange = onCheckedChange, Modifier.scale(0.8f))
        Spacer(Modifier.width(5.dp))
        Text(tag)
    }
}