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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object HomeView {

    private class WorkoutListData(
        workouts: Array<Workout> = emptyArray(),
        retrievingWorkouts: Boolean = false,
        workoutsSynced: Boolean = false,
        val appState: AppState,
        val coroutineScope: CoroutineScope
    ) {
        var workouts by mutableStateOf(workouts)
        var retrievingWorkouts by mutableStateOf(retrievingWorkouts)
        var workoutsSynced by mutableStateOf(workoutsSynced)

        fun ensureWorkoutsSynced() {
            if (!workoutsSynced) {
                workoutsSynced = true
                appState.model.workoutManager?.invalidateLocalCache()
                refreshWorkouts()
            }
        }

        fun refreshWorkouts() {
            // TODO: Ensure no undefined behaviour with global coroutine scope
            retrievingWorkouts = true
            appState.model.workoutManager?.getWorkouts(
                dispatcher = Dispatchers.Default,
                coroutineScope = appState.coroutineScope,
                onSuccess = { workouts = it },
                onFail = {
                    println("Workout request error: ${it.toJson()}")
                }, onComplete = {
                    retrievingWorkouts = false
                }
            )
        }
    }

    @Composable
    fun View(appState: AppState) {
        ensureActiveAccount(appState.model, appState::setView)

        val coroutineScope = rememberCoroutineScope()
        val workoutListData = remember { WorkoutListData(appState = appState, coroutineScope = coroutineScope) }

        workoutListData.ensureWorkoutsSynced()

        Column(
            Modifier.padding(5.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(appState.model, appState::setView)
            Button(onClick = {
                appState.setView(ExviView.WorkoutCreation)
            }) {
                Text("Create Workout")
            }
            Body(
                appState,
                workoutListData
            )
        }
    }

    @Composable
    private fun Body(
        appState: AppState,
        workoutListData: WorkoutListData
    ) {
        BoxWithConstraints(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (maxWidth < 600.dp) {
                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Expandable(
                        Modifier.fillMaxWidth(),
                        header = {
                            Text("Your Workouts")
                        }) {
                        WorkoutsView(
                            appState,
                            workoutListData
                        )
                    }
                    Expandable(Modifier.fillMaxWidth(),
                        header = {
                            Text("Your Progress")
                        }) {
                        AccountView(appState.model)
                    }
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Top
                ) {
                    Expandable(
                        Modifier.fillMaxWidth(0.5f),
                        header = {
                            Text("Your Workouts")
                        }) {
                        WorkoutsView(
                            appState,
                            workoutListData
                        )
                    }
                    Expandable(Modifier.fillMaxWidth(),
                        header = {
                            Text("Your Progress")
                        }) {
                        AccountView(appState.model)
                    }
                }
            }
        }
    }

    @Composable
    private fun WorkoutsView(
        appState: AppState,
        workoutListData: WorkoutListData
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            WorkoutListView(
                appState,
                workoutListData
            )
        }
    }

    @Composable
    private fun AccountView(model: Model) {
        Column {
            val bodyStats = model.bodyStats!!
            Text(
                "Body Weight: ${bodyStats.totalMass.value} ${
                    bodyStats.totalMass.unit.toString().lowercase()
                }s"
            )
            Text("Sex: ${bodyStats.sex.toString().lowercase()}")
        }
    }

    @Composable
    private fun TopBar(model: Model, onViewChange: ViewChangeFun) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                "Welcome, ${model.activeAccount!!.formattedUsername}!",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            SignOutButton(model, onViewChange)
        }
    }

    @Composable
    private fun SignOutButton(
        model: Model,
        onViewChange: ViewChangeFun
    ) {
        IconButton(onClick = {
            onViewChange(ExviView.Login, ::noArgs)
            model.accountManager.signOut()
        }) {
            Icon(Icons.Default.ExitToApp, "Sign Out")
        }
    }

    @Composable
    private fun WorkoutListView(
        appState: AppState,
        wld: WorkoutListData
    ) {

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (wld.retrievingWorkouts) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Fetching your workouts...")
                    CircularProgressIndicator(Modifier.padding(10.dp))
                }
            } else {
                IconButton(onClick = {
                    wld.retrievingWorkouts = true
                    appState.model.workoutManager!!.invalidateLocalCache()
                    wld.refreshWorkouts()
                }) {
                    Icon(Icons.Default.Refresh, "Refresh Workout List")
                }
            }

            if (!wld.retrievingWorkouts) {
                LazyColumn {
                    if (wld.workouts.isNotEmpty()) {
                        items(wld.workouts.size) {
                            WorkoutListViewItem(
                                appState.model,
                                appState::setView,
                                wld.workouts[it],
                                wld::refreshWorkouts
                            )
                        }
                    } else {
                        item {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("You have no workouts", textAlign = TextAlign.Center)
                                Button(onClick = {
                                    appState.setView(ExviView.WorkoutCreation)
                                }) {
                                    Text(
                                        if (appState.previousView == ExviView.Signup)
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

    @Composable
    private fun WorkoutListViewItem(
        model: Model,
        onViewChange: ViewChangeFun,
        workout: Workout,
        refreshWorkouts: () -> Unit
    ) {
        var deleteConfirmEnabled by remember { mutableStateOf(false) }
        val onDeleteConfirmEnabledChanged: (Boolean) -> Unit = { deleteConfirmEnabled = it }

        var deletingWorkout by rememberSaveable { mutableStateOf(false) }
        val onDeletingWorkoutChanged: (Boolean) -> Unit = { deletingWorkout = it }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                workout.name,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            IconButton(onClick = {
                onViewChange(ExviView.ActiveWorkout, workout::newActiveWorkout)
            }, enabled = !deletingWorkout) {
                Icon(Icons.Default.PlayArrow, "Start Workout")
            }
            IconButton(onClick = {
                onViewChange(ExviView.WorkoutCreation) {
                    workout
                }
            }, enabled = !deletingWorkout) {
                Icon(Icons.Default.Edit, "Edit Workout")
            }
            if (!deleteConfirmEnabled) {
                IconButton(onClick = {
                    onDeleteConfirmEnabledChanged(true)
                }, enabled = !deletingWorkout) {
                    Icon(Icons.Default.Delete, "Delete Workout")
                }
            } else {
                IconButton(onClick = {
                    onDeleteConfirmEnabledChanged(false)
                    onDeletingWorkoutChanged(true)
                    model.workoutManager!!.deleteWorkouts(arrayOf(workout.id.get()),
                        onFail = {
                            println(it.toJson())
                        }, onComplete = {
                            onDeletingWorkoutChanged(false)
                            refreshWorkouts()
                        })
                }, enabled = !deletingWorkout) {
                    Icon(Icons.Default.Close, "Delete Workout")
                }
            }
            if (deletingWorkout) {
                CircularProgressIndicator()
            }
        }
    }
}