package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.util.None
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.*
import kotlinx.serialization.json.*

typealias ArgProviderFun = () -> SelfSerializable
typealias ViewChangeFun = (ExviView, provider: ArgProviderFun) -> Unit
typealias ViewFun = @Composable (AppState) -> Unit

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val appState by rememberSaveable(stateSaver = AppState.saver(coroutineScope)) {
        mutableStateOf(AppState(coroutineScope = coroutineScope))
    }

    appState.currentView.compose(appState)
}

enum class ExviView(
    private val viewFun: ViewFun
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
    ActiveWorkout(@Composable {
        ActiveWorkoutView.View(it)
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
    val model: Model = Model(),
    currentView: ExviView = ExviView.Login,
    previousView: ExviView = ExviView.None,
    provided: SelfSerializable = None,
    val coroutineScope: CoroutineScope
) {
    var currentView by mutableStateOf(currentView)
        private set
    var previousView by mutableStateOf(previousView)
        private set
    var provided by mutableStateOf(provided)
        private set

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
        @Suppress("UNCHECKED_CAST")
        fun saver(coroutineScope: CoroutineScope) = mapSaver(
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
                    Json.decodeFromString(it["model"] as String),
                    it["currView"] as ExviView,
                    it["prevView"] as ExviView,
                    selfSerializableFromMap(it["provided"] as Map<String, Any?>),
                    coroutineScope
                )
            }
        )

        @Composable
        fun testAppState(): AppState = AppState(coroutineScope = rememberCoroutineScope())
    }

}