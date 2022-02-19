package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    ensureActiveAccount(model, onViewChange)

    var workouts by rememberSaveable { mutableStateOf(emptyArray<Workout>()) }
    val onWorkoutsChanged: (Array<Workout>) -> Unit = { workouts = it }

    var retrievingWorkouts by rememberSaveable { mutableStateOf(false) }
    val onRetrievingWorkoutsChanged: (Boolean) -> Unit = { retrievingWorkouts = it }

    var switchingView by rememberSaveable { mutableStateOf(false) }
    val onSwitchingViewChange: (Boolean) -> Unit = { switchingView = it }

    val refreshWorkouts = {
        onRetrievingWorkoutsChanged(true)
        model.workoutManager!!.getWorkouts(
            onSuccess = onWorkoutsChanged,
            onFail = {
                println(it.toJson())
            }, onComplete = {
                onRetrievingWorkoutsChanged(false)
            }
        )
    }

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
        Button(onClick = {
            onViewChange(ExviView.WorkoutCreation, ::noArgs)
        }) {
            Text("Create Workout")
        }
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
                onSwitchingViewChange,
                refreshWorkouts
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
    onSwitchingViewChange: (Boolean) -> Unit,
    refreshWorkouts: () -> Unit
) {

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
                refreshWorkouts()
            }) {
                Icon(Icons.Default.Refresh, "Refresh Workout List")
            }
        }

        if (!retrievingWorkouts && !switchingView) {
            LazyColumn {
                if (workouts.isNotEmpty()) {
                    items(workouts.size) {
                        var deleteConfirmEnabled by remember { mutableStateOf(false) }
                        val onDeleteConfirmEnabledChanged: (Boolean) -> Unit = { deleteConfirmEnabled = it }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            Text(
                                "${workouts[it].name}",
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.PlayArrow, "Start Workout")
                            }
                            IconButton(onClick = {
                                onViewChange(ExviView.WorkoutCreation) {
                                    workouts[it]
                                }
                            }) {
                                Icon(Icons.Default.Edit, "Edit Workout")
                            }
                            if (!deleteConfirmEnabled) {
                                IconButton(onClick = {
                                    onDeleteConfirmEnabledChanged(true)
                                }) {
                                    Icon(Icons.Default.Delete, "Delete Workout")
                                }
                            } else {
                                IconButton(onClick = {
                                    onDeleteConfirmEnabledChanged(false)
                                    model.workoutManager!!.deleteWorkouts(arrayOf(workouts[it].id.get()),
                                        onFail = {
                                            println(it.toJson())
                                        }, onComplete = {
                                            refreshWorkouts()
                                        })
                                }) {
                                    Icon(Icons.Default.Close, "Delete Workout")
                                }
                            }
                        }
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
                                onViewChange(ExviView.WorkoutCreation, ::noArgs)
                            }, enabled = !switchingView) {
                                Text(
                                    if (sender == ExviView.Signup)
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
}