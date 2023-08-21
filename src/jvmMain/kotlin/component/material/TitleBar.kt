package component.material

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope

@Composable
fun BarButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Button(
        onClick = onClick,
        shape = RectangleShape,
        contentPadding = PaddingValues(9.dp),
        modifier = Modifier.defaultMinSize(50.dp, 1.dp).fillMaxHeight(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, contentColor = Color.DarkGray)
    ) {
        content()
    }
}

@Composable
fun WindowScope.TitleBar(modifier: Modifier,
                         onCloseRequest: () -> Unit,
                         onMinimizeRequest: () -> Unit,
                         onMenuRequest: () -> Unit
) {
    WindowDraggableArea(modifier.fillMaxWidth()) {
        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End) {
                BarButton(onMenuRequest) {
                    Icon(Icons.Sharp.Menu, "")
                }
                BarButton(onMinimizeRequest) {
                    Icon(painter = painterResource("/icons/minimize.svg"), "")
                }
                BarButton(onCloseRequest) {
                    Icon(Icons.Sharp.Close, "")
                }
            }
        }
    }
}