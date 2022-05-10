package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.icons.ExviIcons
import com.camackenzie.exvi.client.model.ComposeActiveWorkout
import com.camackenzie.exvi.core.model.ActiveWorkout
import androidx.compose.ui.text.style.TextOverflow
import com.camackenzie.exvi.core.model.toLocalDate
import com.camackenzie.exvi.core.util.ExviLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.camackenzie.exvi.client.components.*
import com.camackenzie.exvi.core.model.Time
import com.camackenzie.exvi.core.model.seconds

object ActiveWorkoutView : Viewable {

    private class WorkoutData(
        other: ActiveWorkout,
        playing: Boolean = false,
        currentExerciseIndex: Int = 0,
        currentExerciseRemainingTime: Time? = null,
        val coroutineScope: CoroutineScope,
    ) : ComposeActiveWorkout(other) {
        var playing by mutableStateOf(playing)
        var currentExerciseIndex by mutableStateOf(currentExerciseIndex)
        var currentExerciseRemainingTime by mutableStateOf(currentExerciseRemainingTime)
    }

    @Composable
    override fun View(appState: AppState) {
        ensureActiveAccount(appState)
        if (appState.provided !is ActiveWorkout) {
            ExviLogger.w("No active workout provided", tag = "GUI")
            appState.setView(ExviView.Home)
        }

        val coroutineScope = rememberCoroutineScope()
        val workout = remember { WorkoutData(appState.provided as ActiveWorkout, coroutineScope = coroutineScope) }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                workout.name, fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            if (!workout.hasStarted()) {
                Button(onClick = workout::start) {
                    Text("Start Workout")
                }
                Button(onClick = {
                    appState.setView(ExviView.Home)
                }) {
                    Text("Back to Home")
                }
            } else {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        workout.playing = !workout.playing
                    }) {
                        if (!workout.playing) Icon(Icons.Default.PlayArrow, "Play Workout")
                        else Icon(ExviIcons.Stop, "Pause Workout")
                    }
                    Button(onClick = {
                        appState.model.workoutManager!!.putActiveWorkouts(
                            arrayOf(workout),
                            appState.coroutineScope,
                            Dispatchers.Default,
                            onFail = {
                                ExviLogger.e(tag = "ACTIVE_WORKOUT") {
                                    "Could not update \"${workout.name}\": code ${it.statusCode}, \"${
                                        it.body
                                    }\""
                                }
                            },
                            onSuccess = {
                                ExviLogger.i(tag = "ACTIVE_WORKOUT") { "Successfully updated \"${workout.name}\"" }
                            }
                        )
                        appState.setView(ExviView.Home)
                    }) {
                        Text("Save & Exit")
                    }
                }
                Text(
                    workout.name, fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
                if (workout.hasStarted()) {
                    Text("Started ${workout.startTime!!.toLocalDate()}")
                    if (workout.playing) {
                        remember {
                            workout.currentExerciseRemainingTime = 10.seconds
                        }

                        Timer(
                            remainingTime = workout.currentExerciseRemainingTime!!,
                            onRemainingTimeChanged = { workout.currentExerciseRemainingTime = it },
                            coroutineScope = workout.coroutineScope
                        )
                    }
                }
                ActiveExerciseSetRow(appState, workout)
            }
        }
    }

    @Composable
    private fun ActiveExerciseSetRow(
        appState: AppState,
        workoutData: WorkoutData,
    ) {
        LazyColumn(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items(workoutData.exercises) { exerciseSet ->
                Row(
                    Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        exerciseSet.exercise.name,
                        modifier = Modifier.fillMaxWidth(0.4f),
                        overflow = TextOverflow.Ellipsis
                    )
                    RepList(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        exercise = exerciseSet.active,
                        target = exerciseSet.target,
                        onValueChange = { it, reps ->
                            exerciseSet.active.sets[it].reps = reps
                            workoutData.exercises = arrayOf(*workoutData.exercises)
                        }
                    )
                }
            }
        }
    }

}