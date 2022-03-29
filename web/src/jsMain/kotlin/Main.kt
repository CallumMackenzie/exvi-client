import org.jetbrains.skiko.wasm.onWasmReady
import androidx.compose.ui.window.Window
import com.camackenzie.exvi.client.view.App
import com.camackenzie.exvi.client.view.ExviMaterialTheme
import androidx.compose.material.Text

fun main() {
    onWasmReady {
        Window("Falling Balls") {
            Text("HELLO")
        }
    }
}