package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.util.None
import com.camackenzie.exvi.core.util.SelfSerializable

typealias ArgProviderFun = () -> SelfSerializable
typealias ViewChangeFun = (ExviView, provider: ArgProviderFun) -> Unit

enum class ExviView {
    Login,
    Signup,
    Home,
    WorkoutCreation,
    None
}

@Composable
fun App() {
    // Global state
    @Suppress("UNCHECKED_CAST")
    val model by rememberSaveable(stateSaver = SelfSerializableSaver as Saver<Model, Any>) { mutableStateOf(Model()) }

    // Navigation
    var currentView by rememberSaveable { mutableStateOf(ExviView.Login) }
    var previousView by rememberSaveable { mutableStateOf(ExviView.None) }

    @Suppress("UNCHECKED_CAST")
    var provided by rememberSaveable(stateSaver = SelfSerializableSaver) { mutableStateOf<SelfSerializable>(None) }
    val onViewChange: ViewChangeFun = { newView, argProvider ->
        previousView = currentView
        currentView = newView
        provided = argProvider()
    }

    when (currentView) {
        ExviView.Login -> EntryView.View(
            sender = previousView,
            onViewChange = onViewChange,
            model = model
        )
        ExviView.Signup -> SignupView.View(
            sender = previousView,
            onViewChange = onViewChange,
            model = model
        )
        ExviView.Home -> HomeView.View(
            sender = previousView,
            onViewChange = onViewChange,
            model = model
        )
        ExviView.WorkoutCreation -> WorkoutCreationView.View(
            sender = previousView,
            onViewChange = onViewChange,
            model = model,
            provided = provided
        )
        ExviView.None -> Text(
            "Exvi Fitness", fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
    }
}
