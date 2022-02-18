package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Model

typealias ArgProviderFun = () -> Any
typealias ViewChangeFun = (ExviView, provider: ArgProviderFun) -> Unit

enum class ExviView {
    LOGIN,
    SIGNUP,
    HOME,
    WORKOUT_CREATION,
    NONE
}

@Composable
fun App() {
    // Global state
    val model by rememberSaveable(stateSaver = ModelSaver) { mutableStateOf(Model()) }

    // Navigation
    var currentView by rememberSaveable { mutableStateOf(ExviView.LOGIN) }
    var previousView by rememberSaveable { mutableStateOf(ExviView.NONE) }
    var provided by rememberSaveable { mutableStateOf<Any>({}) }
    val onViewChange: ViewChangeFun = { newView, argProvider ->
        previousView = currentView
        currentView = newView
        provided = argProvider()
    }

    when (currentView) {
        ExviView.LOGIN -> EntryView(
            sender = previousView,
            onViewChange = onViewChange,
            model = model
        )
        ExviView.SIGNUP -> SignupView(
            sender = previousView,
            onViewChange = onViewChange,
            model = model
        )
        ExviView.HOME -> HomeView(
            sender = previousView,
            onViewChange = onViewChange,
            model = model
        )
        ExviView.WORKOUT_CREATION -> WorkoutCreationView(
            sender = previousView,
            onViewChange = onViewChange,
            model = model,
            provided = provided
        )
        ExviView.NONE -> Text(
            "Exvi Fitness", fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
    }
}
