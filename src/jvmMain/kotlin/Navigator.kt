import androidx.compose.runtime.Composable
import view.ConnectView
import view.MainView

enum class Navigator {
    MAIN_VIEW,
    CONNECT_VIEW
}


@Composable
fun navigateTo(destination: Navigator) {
    when (destination) {
        Navigator.MAIN_VIEW -> MainView()
        Navigator.CONNECT_VIEW -> ConnectView()
    }
}