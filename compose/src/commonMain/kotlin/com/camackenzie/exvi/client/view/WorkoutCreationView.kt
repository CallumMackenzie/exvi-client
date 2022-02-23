package com.camackenzie.exvi.client.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.ExerciseManager
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.model.*
import com.soywiz.krypto.SecureRandom
import kotlin.math.abs

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
        var exerciseSearchContent by rememberSaveable { mutableStateOf("") }
        val onExerciseSearchContentChange: (String) -> Unit = { exerciseSearchContent = it }
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
        var exercises by rememberSaveable {
            mutableStateOf(
                if (provided::class == Workout::class)
                    (provided as Workout).exercises.toTypedArray()
                else emptyArray<ExerciseSet>()
            )
        }
        val onExercisesChange: (Array<ExerciseSet>) -> Unit = { exercises = it }

        var infoExercise by rememberSaveable { mutableStateOf<Exercise?>(null) }
        val onInfoExerciseChange: (Exercise?) -> Unit = { infoExercise = it }

        BoxWithConstraints(Modifier.fillMaxSize().padding(10.dp)) {
            if (maxWidth < 600.dp) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
                ) {
                    ExviBox {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            WorkoutNameField(workoutName, onWorkoutNameChange)
                            FinishWorkoutButton(model, onViewChange, provided, workoutName, exercises)
                            CancelWorkoutButton(onViewChange, promptCancel, onPromptCancelChange)
                        }
                    }
                    Expandable(
                        Modifier.fillMaxWidth()
                            .heightIn(max = 300.dp),
                        header = {
                            Text("Workout Exercises")
                        }
                    ) {
                        WorkoutExerciseListView(
                            exercises,
                            onExercisesChange,
                            onInfoExerciseChange
                        )
                    }
                    Expandable(
                        Modifier.fillMaxWidth()
                            .heightIn(max = 300.dp),
                        header = {
                            Text("All Exercises")
                        }
                    ) {
                        ExerciseSearchView(
                            model.exerciseManager,
                            exercises,
                            onExercisesChange,
                            exerciseSearchContent,
                            onExerciseSearchContentChange,
                            onInfoExerciseChange
                        )
                    }
                    Expandable(
                        Modifier.fillMaxWidth()
                            .heightIn(max = 400.dp),
                        header = {
                            Text("Exercise Info")
                        }
                    ) {
                        ExerciseInfoView(infoExercise)
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
                ) {
                    ExviBox {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                        ) {
                            WorkoutNameField(workoutName, onWorkoutNameChange)
                            FinishWorkoutButton(model, onViewChange, provided, workoutName, exercises)
                            CancelWorkoutButton(onViewChange, promptCancel, onPromptCancelChange)
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        Expandable(
                            Modifier.fillMaxWidth(0.5f)
                                .heightIn(max = 400.dp),
                            header = {
                                Text("Workout Exercises")
                            }
                        ) {
                            WorkoutExerciseListView(
                                exercises,
                                onExercisesChange,
                                onInfoExerciseChange
                            )
                        }
                        Expandable(
                            Modifier.fillMaxWidth()
                                .heightIn(max = 400.dp),
                            header = {
                                Text("All Exercises")
                            }
                        ) {
                            ExerciseSearchView(
                                model.exerciseManager,
                                exercises, onExercisesChange,
                                exerciseSearchContent,
                                onExerciseSearchContentChange,
                                onInfoExerciseChange
                            )
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        Expandable(
                            Modifier.fillMaxWidth(),
                            header = {
                                Text("Exercise Info")
                            }
                        ) {
                            ExerciseInfoView(infoExercise)
                        }
                    }
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
        workoutName: String,
        exercises: Array<ExerciseSet>
    ) {
        Button(onClick = {
            val baseWorkout = if (provided::class == Workout::class)
                provided as Workout else null
            val newExercises = arrayListOf<ExerciseSet>(*exercises)

            val workout = if (baseWorkout != null) Workout(
                workoutName,
                "",
                newExercises,
                baseWorkout.id
            )
            else Workout(
                workoutName,
                "",
                newExercises
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
            model.workoutManager!!.validateLocalCache()
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
    private fun ExerciseInfoView(
        exercise: Exercise?
    ) {
        if (exercise != null) {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    exercise.name,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )

                if (exercise.hasOverview()) {
                    Text(
                        "Overview", fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(exercise.overview)
                }

                if (exercise.hasDescription()) {
                    Text(
                        "Description", fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(exercise.description)
                }

                if (exercise.hasTips()) {
                    Text(
                        "Tips", fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                    Text(exercise.tips)
                }
            }
        } else {
            Text("No Exercise Selected")
        }
    }

    @Composable
    private fun ExerciseSearchView(
        manager: ExerciseManager,
        workoutExercises: Array<ExerciseSet>,
        onWorkoutExerciseChange: (Array<ExerciseSet>) -> Unit,
        exerciseSearchContent: String,
        onExerciseSearchContentChange: (String) -> Unit,
        onInfoExerciseChange: (Exercise?) -> Unit,
        listViewModifier: Modifier = Modifier.fillMaxSize(),
    ) {
        if (!manager.hasExercises()) {
            manager.loadStandardExercises()
        }
        val allExercises = manager.exercises.toTypedArray()
        allExercises.sortBy {
            var sum = 0
            for (word in exerciseSearchContent.split("\\s+")) {
                sum += if (it.name.contains(word, true)) {
                    it.name.length - word.length
                } else 100
            }
            sum
        }
        Column(
            listViewModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = exerciseSearchContent,
                    onValueChange = {
                        if (!it.contains("\n")) {
                            onExerciseSearchContentChange(it)
                        }
                    },
                    label = { Text("Exercise Name") },
                    placeholder = {
                        Text("Exercise Name")
                    }
                )
            }
            ExviBox {
                LazyColumn(
                    listViewModifier,
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(allExercises.size) {
                        AllExercisesListViewItem(
                            allExercises[it],
                            workoutExercises,
                            onWorkoutExerciseChange,
                            onInfoExerciseChange
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ExviBox(
        content: @Composable () -> Unit
    ) {
        Box(
            Modifier.border(1.dp, Color.Black)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }

    @Composable
    private fun WorkoutExerciseListView(
        exercises: Array<ExerciseSet>,
        onExercisesChange: (Array<ExerciseSet>) -> Unit,
        onInfoExerciseChange: (Exercise?) -> Unit,
        listViewModifier: Modifier = Modifier.fillMaxSize()
    ) {
        ExviBox {
            LazyColumn(
                listViewModifier,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(exercises.size) {
                    WorkoutExerciseListViewItem(exercises[it], it, exercises, onExercisesChange, onInfoExerciseChange)
                }
                if (exercises.isEmpty()) {
                    item {
                        Text("There are no exercises in this workout")
                    }
                }
            }
        }
    }

    @Composable
    private fun AllExercisesListViewItem(
        exercise: Exercise,
        workoutExercises: Array<ExerciseSet>,
        onWorkoutExerciseChange: (Array<ExerciseSet>) -> Unit,
        onInfoExerciseChange: (Exercise?) -> Unit,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(exercise.name)
            IconButton(onClick = {
                onInfoExerciseChange(exercise)
            }) {
                Icon(Icons.Default.Info, "Exercise Info")
            }
            IconButton(onClick = {
                onWorkoutExerciseChange(workoutExercises + arrayOf(ExerciseSet(exercise, "", arrayOf(10, 10, 10))))
            }) {
                Icon(Icons.Default.Add, "Add Exercise")
            }
        }
    }

    @Composable
    private fun WorkoutExerciseListViewItem(
        exercise: ExerciseSet,
        index: Int,
        exercises: Array<ExerciseSet>,
        onExercisesChange: (Array<ExerciseSet>) -> Unit,
        onInfoExerciseChange: (Exercise?) -> Unit,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
        ) {
            Text(exercise.exercise.name)

            IconButton(onClick = {
                onInfoExerciseChange(exercise.exercise)
            }) {
                Icon(Icons.Default.Info, "Exercise Info")
            }
            IconButton(onClick = {
                onExercisesChange(exercises.filterIndexed { i, _ ->
                    i != index
                }.toTypedArray())
            }) {
                Icon(Icons.Default.Close, "Remove Exercise")
            }
        }
    }

}