package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.util.None
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*

typealias ArgProviderFun = () -> SelfSerializable
typealias ViewChangeFun = (ExviView, provider: ArgProviderFun) -> Unit
typealias ViewFun = @Composable (AppState) -> Unit

@Composable
fun App() {
    // Global state
    val appState by rememberSaveable(stateSaver = AppState.Saver) { mutableStateOf(AppState()) }

    appState.currentView.compose(appState)
}

@Serializable
enum class ExviView(
    @kotlinx.serialization.Transient
    private val viewFun: @Composable (AppState) -> Unit
) {
    Login(@Composable {
        EntryView.View(it)
    }),
    Signup(@Composable {
        SignupView.View(it)
    }),
    Home(@Composable {
        HomeView.View(it)
    }),
    WorkoutCreation(@Composable {
        WorkoutCreationView.View(it)
    }),
    AccountSettings(@Composable {
    }),
    None(@Composable {
        Text(
            "Exvi Fitness", fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
    });

    @Composable
    fun compose(appState: AppState) {
        viewFun(appState)
    }
}

class AppState(
    model: Model = Model(),
    currentView: ExviView = ExviView.Login,
    previousView: ExviView = ExviView.None,
    provided: SelfSerializable = None
) {
    var currentView by mutableStateOf(currentView)
        private set
    var previousView by mutableStateOf(previousView)
        private set
    var provided by mutableStateOf(provided)
        private set
    val model = model

    fun setView(view: ExviView, args: ArgProviderFun = ::noArgs) {
        previousView = currentView
        currentView = view
        provided = args()
    }

    fun setView(view: ExviView, args: SelfSerializable) {
        setView(view) {
            args
        }
    }

    companion object {
        val Saver = mapSaver<AppState>(
            save = {
                mapOf(
                    "currView" to it.currentView,
                    "prevView" to it.previousView,
                    "provided" to selfSerializableToMap(it.provided),
                    "model" to it.model.toJson()
                )
            },
            restore = {
                AppState(
                    Json.decodeFromString<Model>(it["model"] as String),
                    it["currView"] as ExviView,
                    it["prevView"] as ExviView,
                    selfSerializableFromMap(it["provided"] as Map<String, Any?>)
                )
            }
        )
    }

}