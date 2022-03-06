package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Account
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.client.rendering.Camera3D
import com.camackenzie.exvi.client.rendering.Renderer3D
import com.camackenzie.exvi.core.util.None
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.camackenzie.exvi.client.rendering.*
import com.camackenzie.exvi.client.model.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.PointMode
import com.soywiz.korma.geom.*
import kotlinx.coroutines.launch

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
//        Text(
//            "Exvi Fitness", fontSize = 30.sp,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.padding(10.dp)
//        )

        val camera = remember {
            Camera3D(
                position = Vector3D().setTo(0.0, 0.0, 0.0)
            )
        }
        var ctr by remember { mutableStateOf(0) }
        var mesh = remember { Mesh3D.fromObj(readTextFile("cube.obj")) }
        val renderer = Renderer3D(camera = camera)

        val scope = androidx.compose.runtime.rememberCoroutineScope()
        val job = remember {
            scope.launch {
                while (true) {
                    kotlinx.coroutines.delay(10)
                    mesh.transform.setToRotation(ctr.radians, Vector3D(1.0, 0.0, 0.0))
                        .setToTranslation(
                            0.0, 0.0, 40.0
                        )
                    ++ctr
                }
            }
        }

        Text(text = ctr.toString())
        Canvas(Modifier.fillMaxSize()) {
            camera.aspectRatio = size.height / size.width

            val pts = renderer.render(mesh)
            drawPoints(
                points = pts.mapIndexed { i, pt ->
//                    println("$i: $pt")
                    Offset(pt.x.toFloat() * size.width, pt.y.toFloat() * size.height)
                },
                PointMode.Points,
                Color.Black,
                strokeWidth = 10f
            )

//            for (pt in renderer.render(mesh)) {
//                val color = Color.Black

//                if (tri.p0.x.isFinite() && tri.p0.y.isFinite()
//                    && tri.p1.x.isFinite() && tri.p1.y.isFinite()
//                )
//                    drawLine(
//                        color = color,
//                        start = Offset(
//                            tri.p0.x.toFloat() * size.width, tri.p0.y.toFloat() * size.height
//                        ),
//                        end = Offset(
//                            tri.p1.x.toFloat() * size.width, tri.p1.y.toFloat() * size.height
//                        )
//                    )
//
//                if (tri.p2.x.isFinite() && tri.p2.y.isFinite()
//                    && tri.p1.x.isFinite() && tri.p1.y.isFinite()
//                )
//                    drawLine(
//                        color = color,
//                        start = Offset(
//                            tri.p1.x.toFloat() * size.width, tri.p1.y.toFloat() * size.height
//                        ),
//                        end = Offset(
//                            tri.p2.x.toFloat() * size.width, tri.p2.y.toFloat() * size.height
//                        )
//                    )
//
//                if (tri.p0.x.isFinite() && tri.p0.y.isFinite()
//                    && tri.p2.x.isFinite() && tri.p2.y.isFinite()
//                )
//                    drawLine(
//                        color = color,
//                        start = Offset(
//                            tri.p2.x.toFloat() * size.width, tri.p2.y.toFloat() * size.height
//                        ),
//                        end = Offset(
//                            tri.p0.x.toFloat() * size.width, tri.p0.y.toFloat() * size.height
//                        )
//                    )
//            }
        }
    });

    @Composable
    fun compose(appState: AppState) {
        viewFun(appState)
    }
}

class AppState(
    val model: Model = Model(),
    currentView: ExviView = ExviView.None,
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

    val settings
        get() = model.settings

    init {
        if (settings.hasKey("activeUser")) {
            model.accountManager.activeAccount = Account.fromCrendentialsString(settings.getString("activeUser"))
            setView(ExviView.Home)
        }
    }

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