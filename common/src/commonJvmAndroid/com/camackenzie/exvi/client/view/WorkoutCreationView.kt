package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.model.Workout
import com.soywiz.krypto.SecureRandom

val workoutNamePresets = arrayOf(
    "Pull Day", "Push Day", "Leg Day", "Chest Day",
    "Bicep Bonanza", "Quad Isolation", "Calf Cruncher",
    "Forearm Fiesta", "The Quadfather", "Quadzilla",
    "Shoulders", "Back Builder", "Core", "Cardio Day 1"
)

@Composable
fun WorkoutCreationView(
    sender: ExviView,
    onViewChange: ViewChangeFun,
    model: Model,
    provided: Any
) {
    ensureActiveAccount(model, onViewChange)

    var promptCancel by rememberSaveable { mutableStateOf(false) }
    val onPromptCancelChange: (Boolean) -> Unit = { promptCancel = it }

    var workoutName by rememberSaveable {
        mutableStateOf(
            if (provided::class == Workout::class)
                (provided as Workout).name else
                "New Workout"
        )
    }
    val onWorkoutNameChange: (String) -> Unit = { workoutName = it }

    var workoutDescription by rememberSaveable {
        mutableStateOf(
            if (provided::class == Workout::class)
                (provided as Workout).name else
                ""
        )
    }
    val onWorkoutDescriptionChange: (String) -> Unit = { workoutDescription = it }

    // Control bar
    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(value = workoutName,
            label = { Text("Workout Name") },
            placeholder = {
                Text(workoutNamePresets[(SecureRandom.nextDouble() * workoutNamePresets.size).toInt()])
            },
            onValueChange = {
                if (it.length <= 30) {
                    onWorkoutNameChange(it)
                }
            })
        Button(onClick = {
            val baseWorkout = if (provided::class == Workout::class)
                provided as Workout else null

            val workout = if (baseWorkout != null) Workout(
                workoutName,
                "",
                ArrayList(),
                baseWorkout.id
            )
            else Workout(
                workoutName,
                "",
                ArrayList()
            )
            model.workoutManager!!.putWorkouts(
                arrayOf(workout),
                onFail = {
                    println(it.toJson())
                },
                onComplete = {
                    model.workoutManager!!.invalidateLocalCache()
                }
            )
            onViewChange(ExviView.Home, ::noArgs)
        }) {
            Text("Finish")
        }
        if (!promptCancel) {
            Button(onClick = {
                onPromptCancelChange(true)
            }) {
                Text("Cancel")
            }
        } else {
            Button(onClick = {
                onPromptCancelChange(false)
            }) {
                Text("Keep Editing")
            }
            Button(onClick = {
                onViewChange(ExviView.Home, ::noArgs)
            }) {
                Text("Cancel & Lose Changes")
            }
        }
    }

}