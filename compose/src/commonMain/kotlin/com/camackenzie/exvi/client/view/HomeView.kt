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
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.model.Workout

object HomeView {
    @Composable
    fun View(
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

        var workoutsSynced by rememberSaveable { mutableStateOf(false) }
        val onWorkoutsSyncedChanged: (Boolean) -> Unit = { workoutsSynced = it }

        val refreshWorkouts: () -> Unit = {
            onRetrievingWorkoutsChanged(true)
            model.workoutManager?.getWorkouts(
                onSuccess = onWorkoutsChanged,
                onFail = {
                    println(it.toJson())
                }, onComplete = {
                    onRetrievingWorkoutsChanged(false)
                }
            )
        }

        if (!workoutsSynced) {
            onWorkoutsSyncedChanged(true)
            model.workoutManager?.invalidateLocalCache()
            refreshWorkouts()
        }

        Column(
            Modifier.padding(5.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(model, onViewChange)
            Button(onClick = {
                onViewChange(ExviView.WorkoutCreation, ::noArgs)
            }) {
                Text("Create Workout")
            }
            Body(
                sender,
                model,
                onViewChange,
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

    @Composable
    private fun Body(
        sender: ExviView, model: Model,
        onViewChange: ViewChangeFun,
        workouts: Array<Workout>,
        onWorkoutsChanged: (Array<Workout>) -> Unit,
        retrievingWorkouts: Boolean,
        onRetrievingWorkoutsChanged: (Boolean) -> Unit,
        switchingView: Boolean,
        onSwitchingViewChange: (Boolean) -> Unit,
        refreshWorkouts: () -> Unit
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
                            sender,
                            model,
                            onViewChange,
                            workouts,
                            onWorkoutsChanged,
                            retrievingWorkouts,
                            onRetrievingWorkoutsChanged,
                            switchingView,
                            onSwitchingViewChange,
                            refreshWorkouts
                        )
                    }
                    Expandable(Modifier.fillMaxWidth(),
                        header = {
                            Text("Your Progress")
                        }) {
                        AccountView(model)
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
                            sender,
                            model,
                            onViewChange,
                            workouts,
                            onWorkoutsChanged,
                            retrievingWorkouts,
                            onRetrievingWorkoutsChanged,
                            switchingView,
                            onSwitchingViewChange,
                            refreshWorkouts
                        )
                    }
                    Expandable(Modifier.fillMaxWidth(),
                        header = {
                            Text("Your Progress")
                        }) {
                        AccountView(model)
                    }
                }
            }
        }
    }

    @Composable
    private fun WorkoutsView(
        sender: ExviView, model: Model,
        onViewChange: ViewChangeFun,
        workouts: Array<Workout>,
        onWorkoutsChanged: (Array<Workout>) -> Unit,
        retrievingWorkouts: Boolean,
        onRetrievingWorkoutsChanged: (Boolean) -> Unit,
        switchingView: Boolean,
        onSwitchingViewChange: (Boolean) -> Unit,
        refreshWorkouts: () -> Unit
    ) {
        Row(
            Modifier.fillMaxWidth(),
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

    @Composable
    private fun AccountView(model: Model) {
        Column {
            val bodyStats = model.bodyStats!!
            Text(
                "Body Weight: ${bodyStats.totalMass.value} ${
                    bodyStats.totalMass.unit.toString().lowercase()
                }s"
            )
            Text("Sex: ${bodyStats!!.sex.toString().lowercase()}")
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
            Modifier.fillMaxWidth(),
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
                    model.workoutManager!!.invalidateLocalCache()
                    refreshWorkouts()
                }) {
                    Icon(Icons.Default.Refresh, "Refresh Workout List")
                }
            }

            if (!retrievingWorkouts && !switchingView) {
                LazyColumn {
                    if (workouts.isNotEmpty()) {
                        items(workouts.size) {
                            WorkoutListViewItem(
                                model,
                                onViewChange,
                                workouts[it],
                                refreshWorkouts
                            )
                        }
                    } else {
                        item {
                            NoWorkoutsPrompt(
                                onViewChange,
                                onSwitchingViewChange,
                                sender
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NoWorkoutsPrompt(
        onViewChange: ViewChangeFun,
        onSwitchingViewChange: (Boolean) -> Unit,
        sender: ExviView
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("You have no workouts", textAlign = TextAlign.Center)
            Button(onClick = {
                onSwitchingViewChange(true)
                onViewChange(ExviView.WorkoutCreation, ::noArgs)
            }) {
                Text(
                    if (sender == ExviView.Signup)
                        "Create your first workout" else
                        "Create a new workout"
                )
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
                "${workout.name}",
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            IconButton(onClick = {}, enabled = !deletingWorkout) {
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