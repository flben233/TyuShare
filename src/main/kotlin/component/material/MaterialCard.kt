package component.material

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 主界面功能卡片的基本样式组件
 * @author ShirakawaTyu
 * @since 9/17/2023 4:58 PM
 * @version 1.0
 */
@Composable
fun MaterialCard(title: String, description: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(modifier = modifier.padding(top = 15.dp)) {
        Column(Modifier.padding(15.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(description)
            Spacer(Modifier.height(5.dp))
            content()
        }
    }
}