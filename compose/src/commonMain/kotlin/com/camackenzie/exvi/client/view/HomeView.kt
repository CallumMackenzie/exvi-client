package com.camackenzie.exvi.client.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.components.AlertDialog
import com.camackenzie.exvi.client.components.LoadingIcon
import com.camackenzie.exvi.core.api.WorkoutListRequest
import com.camackenzie.exvi.core.model.ActiveWorkout
import com.camackenzie.exvi.core.model.Workout
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
        ActiveWorkoutListView(appState, wld)
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
    private fun ActiveWorkoutListView(
        appState: AppState, wld: WorkoutListData
    ) {
        val serverWorkoutManager = appState.model.workoutManager!!.serverManager
        wld.ensureWorkoutsSynced()

        if (wld.retrievingActiveWorkouts
            || serverWorkoutManager.fetchingActiveWorkouts
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Fetching your active workouts...")
                LoadingIcon()
            }
        }

        LazyColumn {
            if (wld.activeWorkouts.isNotEmpty()) {
                items(wld.activeWorkouts) { wk ->
                    var deleteConfirmEnabled by remember { mutableStateOf(false) }
                    var deletingWorkout by rememberSaveable { mutableStateOf(false) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.Start)
                    ) {
                        Text(wk.name, fontSize = 20.sp)
                        IconButton(onClick = {
                            appState.setView(ExviView.ActiveWorkout, wk)
                        }, enabled = !deletingWorkout) {
                            Icon(Icons.Default.PlayArrow, "Resume workout")
                        }
                        IconButton(onClick = {
                            deleteConfirmEnabled = true
                        }, enabled = !deletingWorkout) {
                            Icon(Icons.Default.Delete, "Delete Active Workout")
                        }
                        if (deletingWorkout) LoadingIcon()

                        if (deleteConfirmEnabled) {
                            AlertDialog(
                                modifier = Modifier.border(1.dp, MaterialTheme.colors.primary),
                                onDismissRequest = {
                                    deleteConfirmEnabled = false
                                },
                                title = {
                                    Text(text = "Delete \"${wk.name}\"?")
                                },
                                buttons = {
                                    Row(
                                        Modifier.padding(5.dp),
                                        horizontalArrangement = Arrangement.spacedBy(
                                            5.dp,
                                            Alignment.CenterHorizontally
                                        ),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Button(onClick = {
                                            deletingWorkout = true
                                            deleteConfirmEnabled = false
                                            appState.model.workoutManager!!.deleteActiveWorkouts(arrayOf(wk.activeWorkoutId.get()),
                                                onFail = {
                                                    ExviLogger.e(
                                                        "Error code ${it.statusCode}: ${it.body}",
                                                        tag = "CLIENT"
                                                    )
                                                }, onComplete = {
                                                    deletingWorkout = false
                                                    wld.refreshWorkouts()
                                                })
                                        }) {
                                            Text("Delete")
                                        }
                                        Button(onClick = { deleteConfirmEnabled = false }) {
                                            Text("Cancel")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            } else if (!wld.retrievingActiveWorkouts) {
                item {
                    Text("No recent active workouts")
                }
            }
        }
    }


    @Composable
    private fun WorkoutListView(
        appState: AppState,
        wld: WorkoutListData
    ) {
        val serverWorkoutManager = appState.model.workoutManager!!.serverManager
        wld.ensureWorkoutsSynced()
        LazyColumn {
            item {
                if (wld.retrievingWorkouts
                    || serverWorkoutManager.fetchingWorkouts
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
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
            }

            if (wld.workouts.isNotEmpty()) {
                items(wld.workouts.size) {
                    WorkoutListViewItem(
                        appState,
                        wld,
                        wld.workouts[it],
                    )
                }
            } else if (!wld.retrievingWorkouts) {
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

    @Composable
    private fun WorkoutListViewItem(
        appState: AppState,
        wld: WorkoutListData,
        workout: Workout
    ) {
        var deleteConfirmEnabled by remember { mutableStateOf(false) }
        var deletingWorkout by rememberSaveable { mutableStateOf(false) }

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
                IconButton(onClick = {
                    deleteConfirmEnabled = true
                }, enabled = !deletingWorkout) {
                    Icon(Icons.Default.Delete, "Delete Workout")
                }
                if (deletingWorkout) LoadingIcon()

                if (deleteConfirmEnabled) {
                    AlertDialog(
                        modifier = Modifier.border(1.dp, MaterialTheme.colors.primary),
                        onDismissRequest = {
                            deleteConfirmEnabled = false
                        },
                        title = {
                            Text(text = "Delete \"${workout.name}\"?")
                        },
                        buttons = {
                            Row(
                                Modifier.padding(5.dp),
                                horizontalArrangement = Arrangement.spacedBy(
                                    5.dp,
                                    Alignment.CenterHorizontally
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Button(onClick = {
                                    deleteConfirmEnabled = false
                                    deletingWorkout = true
                                    appState.model.workoutManager!!.deleteWorkouts(arrayOf(workout.id.get()),
                                        onFail = {
                                            ExviLogger.e("Error code ${it.statusCode}: ${it.body}", tag = "CLIENT")
                                        }, onComplete = {
                                            deletingWorkout = false
                                            wld.refreshWorkouts()
                                        })
                                }, enabled = !deletingWorkout) {
                                    Text("Delete")
                                }
                                Button(onClick = { deleteConfirmEnabled = false }) {
                                    Text("Cancel")
                                }
                            }
                        }
                    )
                }
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
                    type = WorkoutListRequest.Type.ListAllTemplates,
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
                    type = WorkoutListRequest.Type.ListAllActive,
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