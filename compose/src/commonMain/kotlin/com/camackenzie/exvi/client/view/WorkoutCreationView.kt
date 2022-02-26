package com.camackenzie.exvi.client.view

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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.*
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.core.model.*
import com.soywiz.krypto.SecureRandom
import kotlinx.coroutines.*

object WorkoutCreationView {

    private val workoutNamePresets = arrayOf(
        "Pull Day", "Push Day", "Leg Day", "Chest Day",
        "Bicep Bonanza", "Quad Isolation", "Calf Cruncher",
        "Forearm Fiesta", "The Quadfather", "Quadzilla",
        "Shoulders", "Back Builder", "Core", "Cardio Day 1",
        "Deltoid Destroyer", "Shoulder Shredder", "Core Killer",
        "More Core", "Roko's Rhomboids"
    )

    private val generators = mapOf(
        "Random" to { emptyArray() },
        "Arms" to WorkoutGenerator::armPriorities,
        "Legs" to WorkoutGenerator::legPriorities
    )

    private data class ViewData(
        val sender: ExviView,
        val onViewChange: ViewChangeFun,
        val model: Model,
        val provided: Any
    ) {
        fun setView(view: ExviView, provider: ArgProviderFun) {
            onViewChange(view, provider)
        }
    }

    private data class WorkoutData(
        var name: MutableState<String>,
        var description: MutableState<String>,
        var exercises: MutableState<Array<ExerciseSet>>,
        var infoExercise: MutableState<Exercise?>,
        val provided: Any,
        var params: MutableState<WorkoutGeneratorParams> = mutableStateOf(WorkoutGeneratorParams(providers = generators["Arms"]!!.invoke())),
        var lockedExercises: MutableState<Set<Int>> = mutableStateOf(setOf())
    ) {
        constructor(
            name: String,
            description: String,
            exercises: Array<ExerciseSet>,
            selectedExercise: Exercise?,
            provided: Any
        ) : this(
            mutableStateOf(name),
            mutableStateOf(description),
            mutableStateOf(exercises),
            mutableStateOf(selectedExercise),
            provided,
        )

        constructor(provided: Any)
                : this(
            if (provided is Workout) provided.name else "New Workout",
            if (provided is Workout) provided.description else "",
            if (provided is Workout) provided.exercises.toTypedArray() else emptyArray<ExerciseSet>(),
            null,
            provided
        )

        fun createWorkout(): Workout {
            return if (provided is Workout)
                Workout(name.value, description.value, arrayListOf(*exercises.value), provided.id)
            else
                Workout(name.value, description.value, arrayListOf(*exercises.value))
        }

        fun addExercise(ex: ExerciseSet) {
            exercises.value += ex
        }

        fun removeExercise(index: Int) {
            lockExercise(index, false)
            exercises.value = exercises.value.filterIndexed { i, _ ->
                i != index
            }.toTypedArray()
        }

        fun lockExercise(index: Int, lock: Boolean) {
            if (lock) {
                lockedExercises.value = setOf(*lockedExercises.value.toTypedArray(), index)
            } else {
                lockedExercises.value = lockedExercises.value.filter {
                    it != index
                }.toSet()
            }
        }
    }

    private data class WorkoutSearchData(
        var searchContent: MutableState<String>
    ) {
        constructor(searchContent: String = "") : this(mutableStateOf(searchContent))
    }

    private data class SelectorViewData(
        var rightPane: MutableState<String>
    ) {
        constructor(rightPane: String = "Info") : this(mutableStateOf(rightPane))
    }

    @Composable
    fun View(
        sender: ExviView,
        onViewChange: ViewChangeFun,
        model: Model,
        provided: Any
    ) {
        ensureActiveAccount(model, onViewChange)

        val coroutineScope = rememberCoroutineScope()

        val viewData = ViewData(sender, onViewChange, model, provided)
        val workoutData by rememberSaveable { mutableStateOf(WorkoutData(provided)) }
        val workoutSearchData by rememberSaveable { mutableStateOf(WorkoutSearchData()) }
        val selectorViewData by rememberSaveable { mutableStateOf(SelectorViewData()) }

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
                            WorkoutNameField(workoutData)
                            FinishWorkoutButton(viewData, workoutData)
                            CancelWorkoutButton(onViewChange)
                        }
                    }
                    Box(
                        Modifier.fillMaxWidth()
                            .fillMaxHeight(0.4f)
                    ) {
                        WorkoutExerciseListView(workoutData)
                    }
                    ExviBox(Modifier.fillMaxSize()) {
                        ViewSetOne(
                            viewData,
                            workoutData,
                            workoutSearchData,
                            selectorViewData
                        )
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
                            WorkoutNameField(workoutData)
                            FinishWorkoutButton(viewData, workoutData)
                            CancelWorkoutButton(onViewChange)
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        Box(
                            Modifier.fillMaxWidth(0.5f)
                                .fillMaxHeight()
                        ) {
                            WorkoutExerciseListView(workoutData)
                        }
                        ExviBox(Modifier.fillMaxWidth()) {
                            ViewSetOne(
                                viewData,
                                workoutData,
                                workoutSearchData,
                                selectorViewData
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ViewSetOne(
        viewData: ViewData,
        workoutData: WorkoutData,
        searchData: WorkoutSearchData,
        selectorData: SelectorViewData
    ) {
        StringSelectionView(
            views = hashMapOf(
                "Search" to {
                    ExerciseSearchView(viewData, workoutData, searchData)
                },
                "Info" to {
                    ExerciseInfoView(workoutData.infoExercise.value)
                },
                "Generator" to {
                    WorkoutGeneratorView(viewData, workoutData)
                }
            ),
            currentView = selectorData.rightPane.value,
            onCurrentViewChange = {
                selectorData.rightPane.value = it
            }
        )
    }

    private fun constructWorkout(
        provided: Any,
        workoutName: String,
        workoutDescription: String,
        exercises: Array<ExerciseSet>
    ): Workout {
        val baseWorkout = if (provided::class == Workout::class)
            provided as Workout else null
        val newExercises = arrayListOf<ExerciseSet>(*exercises)
        return if (baseWorkout != null) Workout(
            workoutName,
            workoutDescription,
            newExercises,
            baseWorkout.id
        )
        else Workout(
            workoutName,
            workoutDescription,
            newExercises
        )
    }

    @Composable
    private fun WorkoutGeneratorView(
        viewData: ViewData,
        workoutData: WorkoutData
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
        ) {
            Button(onClick = {
                viewData.model.exerciseManager.loadStandardExercisesIfEmpty()
                val generator = WorkoutGenerator(
                    viewData.model.exerciseManager,
                    workoutData.params.value
                )
                val newWorkout = generator.generateWorkout(
                    workoutData.createWorkout(),
                    workoutData.lockedExercises.value.toTypedArray()
                )
                workoutData.exercises.value = newWorkout.exercises.toTypedArray()
            }) {
                Text("Generate")
            }
            Button(onClick = {
                println(workoutData.params.value.toJson())
            }) {
                Text("Check JSON")
            }
        }
    }

    @Composable
    private fun WorkoutNameField(workoutData: WorkoutData) {
        val regex = Regex("([a-zA-Z0-9.]|\\s)*")

        TextField(value = workoutData.name.value,
            label = { Text("Workout Name") },
            placeholder = {
                Text(workoutNamePresets[(SecureRandom.nextDouble() * workoutNamePresets.size).toInt()])
            },
            onValueChange = {
                if (it.length <= 30 && it.matches(regex)) {
                    workoutData.name.value = it
                }
            })
    }

    @Composable
    private fun FinishWorkoutButton(
        viewData: ViewData,
        workoutData: WorkoutData
    ) {
        Button(onClick = {
            viewData.model.workoutManager!!.putWorkouts(
                arrayOf(workoutData.createWorkout()),
                onFail = {
                    println(it.toJson())
                },
                onComplete = {
                    viewData.model.workoutManager!!.invalidateLocalCache()
                }
            )
            viewData.model.workoutManager!!.validateLocalCache()
            viewData.setView(ExviView.Home, ::noArgs)
        }) {
            Text("Finish")
        }
    }

    @Composable
    private fun CancelWorkoutButton(
        onViewChange: ViewChangeFun
    ) {
        var promptCancel by rememberSaveable { mutableStateOf(false) }
        if (!promptCancel) {
            Button(onClick = {
                promptCancel = true
            }) {
                Text("Cancel")
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    promptCancel = false
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
        viewData: ViewData,
        workoutData: WorkoutData,
        searchData: WorkoutSearchData,
        listViewModifier: Modifier = Modifier.fillMaxSize(),
    ) {
        val exerciseManager = viewData.model.exerciseManager
        exerciseManager.loadStandardExercisesIfEmpty()
        val allExercises = exerciseManager.exercises.toTypedArray()
        allExercises.sortBy {
            var sum = 0
            for (word in searchData.searchContent.value.split("\\s+")) {
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
                    value = searchData.searchContent.value,
                    onValueChange = {
                        if (!it.contains("\n")) {
                            searchData.searchContent.value = it
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
                        AllExercisesListViewItem(workoutData, allExercises[it])
                    }
                }
            }
        }
    }

    @Composable
    private fun ExviBox(
        modifier: Modifier? = null,
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = modifier?.then(
                Modifier.border(1.dp, Color.Black)
                    .padding(10.dp)
            ) ?: Modifier.border(1.dp, Color.Black).padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }

    @Composable
    private fun WorkoutExerciseListView(
        workoutData: WorkoutData,
        listViewModifier: Modifier = Modifier.fillMaxSize(),
    ) {
        ExviBox {
            LazyColumn(
                listViewModifier,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(workoutData.exercises.value.size) {
                    WorkoutExerciseListViewItem(workoutData, it)
                }
                if (workoutData.exercises.value.isEmpty()) {
                    item {
                        Text("There are no exercises in this workout")
                    }
                }
            }
        }
    }

    @Composable
    private fun AllExercisesListViewItem(
        workoutData: WorkoutData,
        exercise: Exercise
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(exercise.name)
            IconButton(onClick = {
                workoutData.infoExercise.value = exercise
            }) {
                Icon(Icons.Default.Info, "Exercise Info")
            }
            IconButton(onClick = {
                workoutData.addExercise(ExerciseSet(exercise, "rep", arrayOf(10, 10, 10)))
            }) {
                Icon(Icons.Default.Add, "Add Exercise")
            }
        }
    }

    @Composable
    private fun WorkoutExerciseListViewItem(
        wd: WorkoutData,
        index: Int
    ) {
        val exerciseSet = wd.exercises.value[index]

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
        ) {
            Text(exerciseSet.exercise.name, Modifier.fillMaxWidth(0.5f))

            IconButton(onClick = {
                wd.infoExercise.value = exerciseSet.exercise
            }) {
                Icon(Icons.Default.Info, "Exercise Info")
            }
            Switch(
                checked = wd.lockedExercises.value.contains(index),
                onCheckedChange = {
                    wd.lockExercise(index, it)
                }
            )
            IconButton(onClick = {
                wd.removeExercise(index)
            }) {
                Icon(Icons.Default.Close, "Remove Exercise")
            }
        }
    }

}