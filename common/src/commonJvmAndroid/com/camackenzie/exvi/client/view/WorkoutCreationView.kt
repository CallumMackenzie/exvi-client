package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.camackenzie.exvi.client.model.ExerciseManager
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.model.*
import com.soywiz.krypto.SecureRandom

object WorkoutCreationView {

    private val workoutNamePresets = arrayOf(
        "Pull Day", "Push Day", "Leg Day", "Chest Day",
        "Bicep Bonanza", "Quad Isolation", "Calf Cruncher",
        "Forearm Fiesta", "The Quadfather", "Quadzilla",
        "Shoulders", "Back Builder", "Core", "Cardio Day 1"
    )

    @Composable
    fun View(
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

        var exercises by rememberSaveable { mutableStateOf(emptyArray<Exercise>()) }
        val onExercisesChange: (Array<Exercise>) -> Unit = { exercises = it }

        BoxWithConstraints(Modifier.fillMaxSize()) {
            if (maxWidth < 600.dp) {
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        WorkoutNameField(workoutName, onWorkoutNameChange)
                        FinishWorkoutButton(model, onViewChange, provided, workoutName)
                        CancelWorkoutButton(onViewChange, promptCancel, onPromptCancelChange)
                    }
                    MutableExerciseListView(exercises, onExercisesChange)
                    ExerciseSearchView(model.exerciseManager)
                }
            } else {
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    WorkoutNameField(workoutName, onWorkoutNameChange)
                    FinishWorkoutButton(model, onViewChange, provided, workoutName)
                    CancelWorkoutButton(onViewChange, promptCancel, onPromptCancelChange)
                    MutableExerciseListView(exercises, onExercisesChange)
                    ExerciseSearchView(model.exerciseManager)
                }
            }
        }
    }

    @Composable
    private fun WorkoutNameField(workoutName: String, onWorkoutNameChange: (String) -> Unit) {
        val regex = Regex("([a-zA-Z0-9.]|\\s)*")

        TextField(value = workoutName,
            label = { Text("Workout Name") },
            placeholder = {
                Text(workoutNamePresets[(SecureRandom.nextDouble() * workoutNamePresets.size).toInt()])
            },
            onValueChange = {
                if (it.length <= 30 && it.matches(regex)) {
                    onWorkoutNameChange(it)
                }
            })
    }

    @Composable
    private fun FinishWorkoutButton(
        model: Model,
        onViewChange: ViewChangeFun,
        provided: Any,
        workoutName: String
    ) {
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
    }

    @Composable
    private fun CancelWorkoutButton(
        onViewChange: ViewChangeFun,
        promptCancel: Boolean,
        onPromptCancelChange: (Boolean) -> Unit
    ) {
        if (!promptCancel) {
            Button(onClick = {
                onPromptCancelChange(true)
            }) {
                Text("Cancel")
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    onPromptCancelChange(false)
                }) {
                    Text("Keep Editing")
                }
                Button(onClick = {
                    onViewChange(ExviView.Home, ::noArgs)
                }) {
                    Text("Exit")
                }
            }
        }
    }

    @Composable
    private fun ExerciseSearchView(
        manager: ExerciseManager
    ) {
        ImmutableExerciseListView(manager.exercises.toTypedArray())
    }

    @Composable
    private fun ImmutableExerciseListView(
        exercises: Array<Exercise>
    ) {
        LazyColumn {
            items(exercises.size) {
                ImmutableExerciseListViewItem(exercises[it])
            }
        }
    }

    @Composable
    private fun MutableExerciseListView(
        exercises: Array<Exercise>,
        onExercisesChange: (Array<Exercise>) -> Unit
    ) {
        LazyColumn {
            items(exercises.size) {
                MutableExerciseListViewItem(exercises[it], onExercisesChange)
            }
        }
    }

    @Composable
    private fun MutableExerciseListViewItem(
        exercise: Exercise,
        onExercisesChange: (Array<Exercise>) -> Unit,
    ) {
        Row {
            Text(exercise.name)
        }
    }

    @Composable
    private fun ImmutableExerciseListViewItem(exercise: Exercise) {
        Row {
            Text(exercise.name)
        }
    }
}