import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import org.jetbrains.skiko.wasm.onWasmReady
import androidx.compose.ui.window.Window
import com.camackenzie.exvi.client.view.*

fun main() {
    onWasmReady {
        Window("Test") {
            ExviMaterialTheme {
                var username = rememberSaveable { mutableStateOf("") }
                UsernameField(username.value, { username.value = it })
            }
        }
    }
}