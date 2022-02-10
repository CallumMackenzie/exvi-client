import com.camackenzie.exvi.client.common.App
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Exvi Fitness") {
        MaterialTheme {
            App()
        }
    }
}