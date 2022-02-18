package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun WorkoutCreationView(
    sender: ExviView,
    onViewChange: ViewChangeFun,
    model: Model,
    provided: Any
) {
    EnsureActiveAccount(model, onViewChange)

    var workoutName by rememberSaveable {
        mutableStateOf(
            if (provided::class == Workout::class) {
                (provided as Workout).name
            } else {
                "New Workout"
            }
        )
    }
    val onWorkoutNameChange: (String) -> Unit = { workoutName = it }

    // Control bar
    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(value = workoutName, onValueChange = {
            if (it.length <= 30) {
                onWorkoutNameChange(it)
            }
        })
        Button(onClick = {
            val workout = Workout(workoutName, "", ArrayList())
            model.workoutManager!!.putWorkouts(
                arrayOf(workout),
                onFail = {
                    println(it.toJson())
                },
                onComplete = {
                    model.workoutManager!!.invalidateLocalCache()
                }
            )
            onViewChange(ExviView.HOME) {}
        }) {
            Text("Finish")
        }
        Button(onClick = {
            onViewChange(ExviView.HOME) {}
        }) {
            Text("Cancel")
        }
    }


}