package com.camackenzie.exvi.client.view

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.camackenzie.exvi.client.model.APIInfo
import com.camackenzie.exvi.client.model.Account
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.api.NoneResult
import com.camackenzie.exvi.core.model.ExviSerializer
import com.camackenzie.exvi.core.util.ExviLogger
import com.camackenzie.exvi.core.util.SelfSerializable
import com.camackenzie.exvi.core.util.cached
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

// App version to ensure compatibility with server
private const val APP_VERSION = 6

/**
 * The entry function for the app
 */
@Composable
fun App() {
    // Coroutine scope which persists across the entire application lifecycle
    val coroutineScope = rememberCoroutineScope()
    // Global application state
    val appState by rememberSaveable(stateSaver = AppState.saver(coroutineScope)) {
        mutableStateOf(
            AppState(coroutineScope = coroutineScope)
        )
    }
    // Compose current view
    appState.currentView.compose(appState)
}

/**
 * All possible screens
 */
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
    Error(ErrorView),
    Friends(FriendView),
    InvalidAppVersion(InvalidAppVersionView);

    @Composable
    fun compose(appState: AppState) = viewFun.View(appState)

    @Suppress("UNCHECKED_CAST")
    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>
}

/**
 * A class to manage global application state
 */
class AppState(
    val model: Model = Model(),
    currentView: ExviView = ExviView.Login,
    previousView: ExviView = ExviView.None,
    provided: SelfSerializable = NoneResult(),
    val coroutineScope: CoroutineScope,
    private val processRestartInit: Boolean = false
) {
    // The current view
    var currentView by mutableStateOf(currentView)
        private set

    // The previous view
    var previousView by mutableStateOf(previousView)
        private set

    // The arguments provided to the current view by the previous
    var provided by mutableStateOf(provided)
        private set

    // System-persistent cross-platform application state
    val settings
        get() = model.settings

    init {
        try {
            // If the process is not being restarted and the active user is cached locally
            // then restore the active user and set the view to their home
            if (!processRestartInit && settings.hasKey("activeUser")) {
                model.accountManager.activeAccount = Account.fromCrendentialsString(settings.getString("activeUser"))
                ExviLogger.i { "Restored user session for ${model.activeAccount!!.username}" }
                setView(ExviView.Home)
            }

            // If process is not restarting, ensure app version is compatible with server
            if (!processRestartInit) {
                APIInfo.checkAppCompatibility(APP_VERSION,
                    coroutineScope,
                    onFail = {
                        if (it.statusCode != 418)
                            setView(ExviView.InvalidAppVersion)
                    }, onSuccess = {
                        ExviLogger.i { "App version validated" }
                    })
            }
        } catch (ex: Exception) {
            error(ex)
        }
    }

    /**
     * Tell the application a fatal error has occurred
     */
    fun error(e: Exception) {
        ExviLogger.e(e, tag = "GUI") { "Uncaught Exception: $e" }
        error(e.toString())
    }

    fun error(e: String) {
        setView(ExviView.Error, e.cached())
    }

    /**
     * Should be called when a fatal error occurs in the application
     * to attempt to fix said error
     */
    fun repair() {
        model.repair()
        setView(ExviView.Login)
    }

    /**
     * @param view the view to set
     * @param args a lambda to provide arguments to the new view
     */
    fun setView(view: ExviView, args: () -> SelfSerializable = ::noArgs) {
        previousView = currentView
        currentView = view
        provided = args()
    }

    /**
     * @param view the view to set
     * @param args the arguments to give the new view
     */
    fun setView(view: ExviView, args: SelfSerializable) = setView(view) { args }

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun saver(coroutineScope: CoroutineScope) = mapSaver(
            save = {
                mapOf(
                    "currView" to it.currentView.toJson(),
                    "prevView" to it.previousView.toJson(),
                    "provided" to ExviSerializer.toJson(it.provided),
                    "model" to it.model.toJson()
                )
            },
            restore = {
                AppState(
                    model = ExviSerializer.fromJson(it["model"] as String),
                    currentView = ExviSerializer.fromJson(it["currView"] as String),
                    previousView = ExviSerializer.fromJson(it["prevView"] as String),
                    provided = ExviSerializer.fromJson(it["provided"] as String),
                    coroutineScope = coroutineScope,
                    processRestartInit = true
                )
            }
        )
    }

}