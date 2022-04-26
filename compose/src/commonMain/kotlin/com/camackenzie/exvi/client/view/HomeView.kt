package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.core.model.ActiveWorkout
import com.camackenzie.exvi.core.model.TimeUnit
import com.camackenzie.exvi.core.model.Workout
import com.camackenzie.exvi.core.model.formatToElapsedTime
import com.camackenzie.exvi.core.util.ExviLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

object HomeView : Viewable {

    @Composable
    override fun View(appState: AppState) {
        ensureActiveAccount(appState)

        val coroutineScope = rememberCoroutineScope()
        val workoutListData = remember { WorkoutListData(appState = appState, coroutineScope = coroutineScope) }

        Column(
            Modifier.padding(5.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(appState)
            Button(onClick = {
                appState.setView(ExviView.WorkoutCreation)
            }) {
                Text("Create Workout")
            }
            BoxWithConstraints(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (maxWidth < 600.dp) MobileView(appState, workoutListData)
                else DesktopView(appState, workoutListData)
            }
        }
    }

    @Composable
    private fun MobileView(
        appState: AppState,
        workoutListData: WorkoutListData
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Your Workouts", fontSize = 25.sp)
                WorkoutListView(
                    appState,
                    workoutListData
                )
            }
            Column(
                Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Your Progress", fontSize = 25.sp)
                AccountView(appState, workoutListData)
            }
        }
    }

    @Composable
    private fun DesktopView(
        appState: AppState,
        workoutListData: WorkoutListData
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                Modifier.fillMaxWidth(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Your Workouts", fontSize = 25.sp)
                WorkoutListView(
                    appState,
                    workoutListData
                )
            }
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Your Progress", fontSize = 25.sp)
                AccountView(appState, workoutListData)
            }
        }
    }

    @Composable
    private fun AccountView(appState: AppState, wld: WorkoutListData) {
        wld.ensureWorkoutsSynced()

        if (!wld.retrievingActiveWorkouts) {
            LazyColumn {
                if (wld.activeWorkouts.isEmpty()) {
                    item {
                        Text("No recent active workouts")
                    }
                } else {
                    item {
                        val wk = wld.activeWorkouts[0]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.Start)
                        ) {
                            Text(wk.name, fontSize = 20.sp)
                            if (wk.startTime == null) Text("Has not been started")
                            else Text(
                                "Started ${
                                    (TimeUnit.now() - wk.startTime!!).formatToElapsedTime()
                                } ago"
                            )
                            IconButton(onClick = {
                                appState.setView(ExviView.ActiveWorkout, wk)
                            }) {
                                Icon(Icons.Default.PlayArrow, "Resume workout")
                            }
                        }
                    }
                }
            }
        } else LoadingIcon()
    }

    @Composable
    private fun TopBar(appState: AppState) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                "Welcome, ${appState.model.activeAccount!!.formattedUsername}!",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            SignOutButton(appState)
        }
    }

    @Composable
    private fun SignOutButton(appState: AppState) {
        IconButton(onClick = {
            appState.setView(ExviView.Login)
            appState.model.signOutCurrentAccount()
        }) {
            Icon(Icons.Default.ExitToApp, "Sign Out")
        }
    }

    @Composable
    private fun WorkoutListView(
        appState: AppState,
        wld: WorkoutListData
    ) {
        wld.ensureWorkoutsSynced()

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
                    LoadingIcon()
                }
            } else {
                IconButton(onClick = {
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
                                appState,
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
        appState: AppState,
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

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    appState.setView(ExviView.ActiveWorkout, workout::newActiveWorkout)
                }, enabled = !deletingWorkout) {
                    Icon(Icons.Default.PlayArrow, "Start Workout")
                }
                IconButton(onClick = {
                    appState.setView(ExviView.WorkoutCreation) { workout }
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
                        appState.model.workoutManager!!.deleteWorkouts(arrayOf(workout.id.get()),
                            onFail = {
                                ExviLogger.e("Error code ${it.statusCode}: ${it.body}", tag = "CLIENT")
                            }, onComplete = {
                                onDeletingWorkoutChanged(false)
                                refreshWorkouts()
                            })
                    }, enabled = !deletingWorkout) {
                        Icon(Icons.Default.Close, "Delete Workout")
                    }
                }
                if (deletingWorkout) LoadingIcon()
            }
        }
    }


    private class WorkoutListData(
        workouts: Array<Workout> = emptyArray(),
        activeWorkouts: Array<ActiveWorkout> = emptyArray(),
        retrievingWorkouts: Boolean = false,
        retrievingActiveWorkouts: Boolean = false,
        workoutsSynced: Boolean = false,
        val appState: AppState,
        val coroutineScope: CoroutineScope
    ) {
        var workouts by mutableStateOf(workouts)
        var activeWorkouts by mutableStateOf(activeWorkouts)
        var retrievingWorkouts by mutableStateOf(retrievingWorkouts)
        var retrievingActiveWorkouts by mutableStateOf(retrievingActiveWorkouts)
        var workoutsSynced by mutableStateOf(workoutsSynced)

        fun ensureWorkoutsSynced() {
            // Called during the first composition only
            if (!workoutsSynced) {
                refreshWorkouts()
                workoutsSynced = true
            }
        }

        fun refreshWorkouts() {
            retrievingWorkouts = true
            retrievingActiveWorkouts = true
            coroutineScope.launch {
                joinAll(appState.model.workoutManager!!.getWorkouts(
                    dispatcher = Dispatchers.Default,
                    coroutineScope = coroutineScope,
                    onSuccess = {
                        workouts = it
                    },
                    onFail = {
                        if (it.statusCode != 418)
                            appState.error(Exception("getWorkouts: code ${it.statusCode}: ${it.body}"))
                    },
                    onComplete = {
                        retrievingWorkouts = false
                    }
                ), appState.model.workoutManager!!.getActiveWorkouts(
                    dispatcher = Dispatchers.Default,
                    coroutineScope = coroutineScope,
                    onSuccess = {
                        activeWorkouts = it
                    },
                    onFail = {
                        if (it.statusCode != 418)
                            appState.error(Exception("getActiveWorkouts: code ${it.statusCode}: ${it.body}"))
                    },
                    onComplete = {
                        retrievingActiveWorkouts = false
                    }
                ))
            }
        }
    }
}