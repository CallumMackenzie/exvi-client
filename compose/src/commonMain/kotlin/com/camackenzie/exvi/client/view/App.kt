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
import com.camackenzie.exvi.client.model.Account
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.util.None
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val appState by rememberSaveable(stateSaver = AppState.saver(coroutineScope)) {
        mutableStateOf(AppState(coroutineScope = coroutineScope))
    }

    appState.currentView.compose(appState)
}

@Serializable
enum class ExviView(
    private val viewFun: Viewable
) : SelfSerializable {
    Login(EntryView),
    Signup(SignupView),
    Home(HomeView),
    WorkoutCreation(WorkoutCreationView),
    ActiveWorkout(ActiveWorkoutView),
    None(ErrorView),
    Error(ErrorView);

    @Composable
    fun compose(appState: AppState) = viewFun.View(appState)

    override fun getUID(): String = uid

    override fun toJson(): String = Json.encodeToString(this)

    companion object {
        const val uid = "ExviView"
    }
}

class AppState(
    val model: Model = Model(),
    currentView: ExviView = ExviView.Login,
    previousView: ExviView = ExviView.None,
    provided: SelfSerializable = None,
    val coroutineScope: CoroutineScope,
    private val processRestartInit: Boolean = false
) {
    var currentView by mutableStateOf(currentView)
        private set
    var previousView by mutableStateOf(previousView)
        private set
    var provided by mutableStateOf(provided)
        private set

    val settings
        get() = model.settings

    init {
        try {
            if (!processRestartInit && settings.hasKey("activeUser")) {
                model.accountManager.activeAccount = Account.fromCrendentialsString(settings.getString("activeUser"))
                setView(ExviView.Home)
            }
        } catch (ex: Exception) {
            error(ex)
        }
    }

    fun error(e: Exception) {
        println("Uncaught Exception: $e")
        setView(ExviView.Error)
    }

    fun repair() {
        model.repair()
        setView(ExviView.Login)
    }

    fun setView(view: ExviView, args: () -> SelfSerializable = ::noArgs) {
        previousView = currentView
        currentView = view
        provided = args()
    }

    fun setView(view: ExviView, args: SelfSerializable) = setView(view) { args }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun saver(coroutineScope: CoroutineScope) = mapSaver(
            save = {
                mapOf(
                    "currView" to it.currentView.toJson(),
                    "prevView" to it.previousView.toJson(),
                    "provided" to selfSerializableToMap(it.provided),
                    "model" to it.model.toJson()
                )
            },
            restore = {
                AppState(
                    model = Json.decodeFromString<Model>(it["model"] as String),
                    currentView = Json.decodeFromString(it["currView"] as String),
                    previousView = Json.decodeFromString(it["prevView"] as String),
                    provided = selfSerializableFromMap(it["provided"] as Map<String, Any?>),
                    coroutineScope = coroutineScope,
                    processRestartInit = true
                )
            }
        )
    }

}