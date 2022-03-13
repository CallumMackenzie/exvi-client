package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.camackenzie.exvi.client.model.*
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.core.model.*
import kotlinx.coroutines.*

object ActiveWorkoutView : Viewable {

    private class WorkoutData(
        workout: ActiveWorkout
    ) {
        var workout by mutableStateOf(workout)
    }

    @Composable
    override fun View(appState: AppState) {
        ensureActiveAccount(appState)
        if (appState.provided !is ActiveWorkout) {
            println("No active workout provided")
            appState.setView(ExviView.Home)
        }

        val workoutData = remember { WorkoutData((appState.provided as ActiveWorkout).copy()) }

        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
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
            }
        }
    }

}