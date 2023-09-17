import androidx.compose.runtime.Composable
import view.ConnectView
import view.MainView

/**
 * 当前可导航的View列表
 * @author ShirakawaTyu
 * @since 9/17/2023 5:28 PM
 * @version 1.0
 */
enum class Navigator {
    MAIN_VIEW,
    CONNECT_VIEW
}

/**
 * 显示一个View
 * @param destination 要显示的View
 * @author ShirakawaTyu
 */
@Composable
fun navigateTo(destination: Navigator) {
    when (destination) {
        Navigator.MAIN_VIEW -> MainView()
        Navigator.CONNECT_VIEW -> ConnectView()
    }
}