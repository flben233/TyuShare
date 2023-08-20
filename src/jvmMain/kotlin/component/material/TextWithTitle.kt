package component.material

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TextWithTitle(
    title: String,
    text: String
) {
    Column {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
        Text(text)
    }
}