package component.tray

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrayItem(text: String, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick()
            showMenu.value = false
        },
        shape = RectangleShape,
        contentPadding = PaddingValues(10.dp),
        modifier = Modifier.fillMaxWidth().height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = Color.DarkGray
        )
    ) {
        Text(modifier = Modifier.fillMaxWidth(), fontSize = 12.sp, text = text, textAlign = TextAlign.Start)
    }
}