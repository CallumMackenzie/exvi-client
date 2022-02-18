package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
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
import com.camackenzie.exvi.client.model.Account
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.core.util.cached
import com.camackenzie.exvi.core.util.EncodedStringCache
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.model.Workout

@Composable
fun HomeView(
    sender: ExviView,
    onViewChange: ViewChangeFun,
    model: Model
) {
    if (!model.accountManager.hasActiveAccount()) {
        println("No active account, switching to login view")
        onViewChange(ExviView.LOGIN)
    }

    TopAppBar(title = {
        Text("Home")
    }, actions = {
    })

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome, ${model.accountManager.activeAccount!!.formattedUsername}!",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            WorkoutListView(onViewChange, model)
        }
    }
}

@Composable
fun WorkoutListView(
    onViewChange: ViewChangeFun,
    model: Model
) {
    var workouts by remember { mutableStateOf(emptyArray<Workout>()) }
    var retrievingWorkouts by remember { mutableStateOf(true) }

    model.accountManager.activeAccount!!.workoutManager.getWorkouts(
        onSuccess = {
            workouts = it
        },
        onFail = {
            println(it.toJson())
        }, onComplete = {
            retrievingWorkouts = false
        }
    )

    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (retrievingWorkouts) {
            CircularProgressIndicator(Modifier.padding(10.dp))
        }

        LazyColumn {
            items(workouts.size) {
                Text("${workouts[it].name}")
            }
        }
    }
}