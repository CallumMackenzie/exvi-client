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
import com.camackenzie.exvi.core.model.*

object ActiveWorkoutView : Viewable {

    private class WorkoutData(
        workout: ActiveWorkout,
        playing: Boolean = false
    ) {
        var workout by mutableStateOf(workout)
        var playing by mutableStateOf(playing)
    }

    @Composable
    override fun View(appState: AppState) {
        ensureActiveAccount(appState)
        if (appState.provided !is ActiveWorkout) {
            println("No active workout provided")
            appState.setView(ExviView.Home)
        }

        val workoutData = remember { WorkoutData((appState.provided as ActiveWorkout).copy()) }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                workoutData.workout.name, fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            if (!workoutData.workout.hasStarted()) {
                Button(onClick = {
                    val started = workoutData.workout.copy()
                    started.start()
                    workoutData.workout = started
                }) {
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
                        workoutData.playing = !workoutData.playing
                    }) {
                        if (!workoutData.playing) Icon(Icons.Default.PlayArrow, "Play Workout")
                        else Icon(Icons.Default.Stop, "Pause Workout")
                    }
                }
                Text(
                    workoutData.workout.name, fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
                if (workoutData.workout.hasStarted()) {
                    Text("Started ${workoutData.workout.startTime!!.toLocalDate()}")
                }
                ActiveExerciseSetRow(appState, workoutData)
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
            items(workoutData.workout.exercises.size) { exercise ->
                val exerciseSet = workoutData.workout.exercises[exercise]
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
                            println("setting reps of exercise no. $exercise, set $it to $reps")
                            
                            workoutData.workout.exercises[exercise].active.sets[it].reps = reps
                            workoutData.workout = workoutData.workout.copy()
                        }
                    )
                }
            }
        }
    }

}