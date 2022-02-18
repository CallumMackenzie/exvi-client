package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.model.Workout

@Composable
fun HomeView(
    sender: ExviView,
    onViewChange: ViewChangeFun,
    model: Model
) {
    var workouts by rememberSaveable { mutableStateOf(emptyArray<Workout>()) }
    val onWorkoutsChanged: (Array<Workout>) -> Unit = { workouts = it }

    var retrievingWorkouts by rememberSaveable { mutableStateOf(true) }
    val onRetrievingWorkoutsChanged: (Boolean) -> Unit = { retrievingWorkouts = it }

    var switchingView by rememberSaveable { mutableStateOf(false) }
    val onSwitchingViewChange: (Boolean) -> Unit = { switchingView = it }

    EnsureActiveAccount(model, onViewChange)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome, ${model.activeAccount!!.formattedUsername}!",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            WorkoutListView(
                sender,
                onViewChange,
                model,
                workouts,
                onWorkoutsChanged,
                retrievingWorkouts,
                onRetrievingWorkoutsChanged,
                switchingView,
                onSwitchingViewChange
            )
        }
    }
}

@Composable
fun WorkoutListView(
    sender: ExviView,
    onViewChange: ViewChangeFun,
    model: Model,
    workouts: Array<Workout>,
    onWorkoutsChanged: (Array<Workout>) -> Unit,
    retrievingWorkouts: Boolean,
    onRetrievingWorkoutsChanged: (Boolean) -> Unit,
    switchingView: Boolean,
    onSwitchingViewChange: (Boolean) -> Unit
) {
    val pullWorkouts = {
        if (!switchingView) {
            model.workoutManager!!.getWorkouts(
                onSuccess = onWorkoutsChanged,
                onFail = {
                    println(it.toJson())
                }, onComplete = {
                    onRetrievingWorkoutsChanged(false)
                }
            )
        }
    }

    pullWorkouts()

    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (retrievingWorkouts) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Fetching your workouts...")
                CircularProgressIndicator(Modifier.padding(10.dp))
            }
        } else {
            IconButton(onClick = {
                onRetrievingWorkoutsChanged(true)
                pullWorkouts()
            }) {
                Icon(Icons.Default.Refresh, "Refresh Workout List")
            }
        }

        LazyColumn {
            if (workouts.isNotEmpty() || retrievingWorkouts) {
                items(workouts.size) {
                    Text("${workouts[it].name}")
                }
            } else {
                item {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("You have no workouts", textAlign = TextAlign.Center)
                        Button(onClick = {
                            onSwitchingViewChange(true)
                            onViewChange(ExviView.WORKOUT_CREATION) {}
                        }, enabled = !switchingView) {
                            Text(
                                if (sender == ExviView.SIGNUP)
                                    "Create your first workout" else
                                    "Create a new workout"
                            )
                        }
                    }
                }
            }
        }
    }
}