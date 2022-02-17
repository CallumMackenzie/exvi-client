package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.core.util.cached
import com.camackenzie.exvi.core.util.EncodedStringCache

typealias ViewChangeFun = (ExviView) -> Unit

enum class ExviView {
    LOGIN,
    SIGNUP,
    HOME,
    NONE
}

@Composable
fun App() {
    // Global state
    val model by remember { mutableStateOf(Model()) }

    // Navigation
    var currentView by remember { mutableStateOf(ExviView.LOGIN) }
    var previousView by remember { mutableStateOf(ExviView.NONE) }
    val onViewChange: ViewChangeFun = { newView ->
        previousView = currentView
        currentView = newView
    }

    when (currentView) {
        ExviView.LOGIN -> EntryView(sender = previousView, onViewChange, model = model)
        ExviView.SIGNUP -> SignupView(sender = previousView, onViewChange, model = model)
        ExviView.HOME -> HomeView(sender = previousView, onViewChange, model = model)
        ExviView.NONE -> Text(
            "Exvi Fitness", fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
    }
}
