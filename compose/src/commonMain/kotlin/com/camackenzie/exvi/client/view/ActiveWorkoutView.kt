package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.icons.ExviIcons
import com.camackenzie.exvi.client.model.ComposeActiveWorkout
import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.ExviLogger

object ActiveWorkoutView : Viewable {

    private class WorkoutData(
        other: ActiveWorkout,
        playing: Boolean = false
    ) : ComposeActiveWorkout(other) {
        var playing by mutableStateOf(playing)
    }

    @Composable
    override fun View(appState: AppState) {
        ensureActiveAccount(appState)
        if (appState.provided !is ActiveWorkout) {
            ExviLogger.w("No active workout provided", tag = "GUI")
            appState.setView(ExviView.Home)
        }

        val workout = remember { WorkoutData(appState.provided as ActiveWorkout) }

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
                }
                Text(
                    workout.name, fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
                if (workout.hasStarted()) {
                    Text("Started ${workout.startTime!!.toLocalDate()}")
                }
                ActiveExerciseSetRow(appState, workout)
            }
        }
    }

    @Composable
    private fun ActiveExerciseSetRow(
        appState: AppState,
        workoutData: WorkoutData
    ) {
        LazyColumn(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            items(workoutData.exercises.size) { exercise ->
                val exerciseSet = workoutData.exercises[exercise]
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(exerciseSet.exercise.name)
                    RepList(
                        exercise = exerciseSet.active,
                        target = exerciseSet.target,
                        onValueChange = { it, reps ->
                            workoutData.exercises[exercise].active.sets[it].reps = reps
                        }
                    )
                }
            }
        }
    }

}