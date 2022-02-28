package com.camackenzie.exvi.client.view

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
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.*
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.core.model.*
import com.soywiz.krypto.SecureRandom
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import kotlinx.serialization.*

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
        val appState: AppState,
        val coroutineScope: CoroutineScope
    ) {
        val model
            get() = appState.model
    }

    private class WorkoutData(
        name: String,
        description: String,
        exercises: Array<ExerciseSet>,
        infoExercise: Exercise?,
        provided: Workout?,
        params: WorkoutGeneratorParams = WorkoutGeneratorParams(providers = generators["Arms"]!!.invoke()),
        lockedExercises: Set<Int> = setOf(),
        exerciseProcessRunning: Boolean = false
    ) {
        var name by mutableStateOf(name)
        var description by mutableStateOf(description)
        var exercises by mutableStateOf(exercises)
        var infoExercise by mutableStateOf(infoExercise)
        var params by mutableStateOf(params)
        var lockedExercises by mutableStateOf(lockedExercises)
        var exerciseProcessRunning by mutableStateOf(exerciseProcessRunning)
        val provided = provided

        constructor(provided: Any)
                : this(
            if (provided is Workout) provided.name else "New Workout",
            if (provided is Workout) provided.description else "",
            if (provided is Workout) provided.exercises.toTypedArray() else emptyArray<ExerciseSet>(),
            null,
            if (provided is Workout) provided else null
        )

        fun createWorkout(): Workout {
            return if (provided is Workout)
                Workout(name, description, arrayListOf(*exercises), provided.id)
            else
                Workout(name, description, arrayListOf(*exercises))
        }

        fun addExercise(ex: ExerciseSet) {
            exercises += ex
        }

        fun removeExercise(index: Int) {
            lockExercise(index, false)
            exercises = exercises.filterIndexed { i, _ ->
                i != index
            }.toTypedArray()
        }

        fun lockExercise(index: Int, lock: Boolean) {
            lockedExercises = if (lock) {
                setOf(*lockedExercises.toTypedArray(), index)
            } else {
                lockedExercises.filter {
                    it != index
                }.toSet()
            }
        }
    }

    private class WorkoutSearchData(
        searchContent: String = "",
        exercisesSorted: Boolean = false,
        searchExercises: Array<Exercise> = emptyArray(),
        processRunning: Boolean = false
    ) {
        var searchContent by mutableStateOf(searchContent)
        var exercisesSorted by mutableStateOf(exercisesSorted)
        var searchExercises by mutableStateOf(searchExercises)
        var processRunning by mutableStateOf(processRunning)
    }

    private class SelectorViewData(
        rightPane: String = "Info"
    ) {
        var rightPane by mutableStateOf(rightPane)
    }

    @Composable
    fun View(
        appState: AppState
    ) {
        ensureActiveAccount(appState.model, appState::setView)

        val viewData = ViewData(appState, rememberCoroutineScope())
        val workoutData = remember { WorkoutData(appState.provided) }
        val workoutSearchData = remember { WorkoutSearchData() }
        val selectorViewData = remember { SelectorViewData() }

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
                            CancelWorkoutButton(appState::setView)
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
                            CancelWorkoutButton(appState::setView)
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
                    ExerciseInfoView(workoutData.infoExercise)
                },
                "Generator" to {
                    WorkoutGeneratorView(viewData, workoutData)
                }
            ),
            currentView = selectorData.rightPane,
            onCurrentViewChange = {
                selectorData.rightPane = it
            }
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
                viewData.coroutineScope.launch(Dispatchers.Default) {
                    workoutData.exerciseProcessRunning = true
                    viewData.model.exerciseManager.loadStandardExercisesIfEmpty()
                    val generator = WorkoutGenerator(
                        viewData.model.exerciseManager,
                        workoutData.params
                    )
                    val newWorkout = generator.generateWorkout(
                        workoutData.createWorkout(),
                        workoutData.lockedExercises.toTypedArray()
                    )
                    workoutData.exercises = newWorkout.exercises.toTypedArray()
                    workoutData.exerciseProcessRunning = false
                }
            }, enabled = !workoutData.exerciseProcessRunning) {
                Text("Generate")
            }
            Button(onClick = {
                println(workoutData.params.toJson())
            }) {
                Text("Check JSON")
            }
        }
    }

    @Composable
    private fun WorkoutNameField(workoutData: WorkoutData) {
        val regex = Regex("([a-zA-Z0-9.]|\\s)*")

        TextField(value = workoutData.name,
            label = { Text("Workout Name") },
            placeholder = {
                Text(workoutNamePresets[(SecureRandom.nextDouble() * workoutNamePresets.size).toInt()])
            },
            onValueChange = {
                if (it.length <= 30 && it.matches(regex)) {
                    workoutData.name = it
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
            viewData.appState.setView(ExviView.Home)
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

        if (!searchData.processRunning && searchData.searchExercises.isEmpty()) {
            viewData.coroutineScope.launch(Dispatchers.IO) {
                searchData.processRunning = true
                exerciseManager.loadStandardExercisesIfEmpty()
                searchData.searchExercises = exerciseManager.exercises.toTypedArray()
                searchData.exercisesSorted = false
                searchData.processRunning = false
            }
        }

        if (!searchData.processRunning
            && searchData.searchExercises.isNotEmpty()
            && !searchData.exercisesSorted
        ) {
            viewData.coroutineScope.launch(Dispatchers.Default) {
                searchData.processRunning = true
                searchData.searchExercises.sortBy {
                    var sum = 0
                    for (word in searchData.searchContent.split("\\s+")) {
                        sum += if (it.name.contains(word, true)) {
                            it.name.length - word.length
                        } else 100
                    }
                    sum
                }
                searchData.exercisesSorted = true
                searchData.processRunning = false
            }
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
                    value = searchData.searchContent,
                    onValueChange = {
                        if (!it.contains("\n")) {
                            searchData.searchContent = it
                            searchData.exercisesSorted = false
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
                    if (!searchData.processRunning) {
                        items(searchData.searchExercises.size) {
                            AllExercisesListViewItem(workoutData, searchData.searchExercises[it])
                        }
                    } else {
                        item {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
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
                if (!workoutData.exerciseProcessRunning) {
                    items(workoutData.exercises.size) {
                        WorkoutExerciseListViewItem(workoutData, it)
                    }
                } else {
                    item {
                        CircularProgressIndicator()
                    }
                }
                if (workoutData.exercises.isEmpty()
                    && !workoutData.exerciseProcessRunning
                ) {
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
                workoutData.infoExercise = exercise
            }) {
                Icon(Icons.Default.Info, "Exercise Info")
            }
            IconButton(onClick = {
                workoutData.addExercise(
                    ExerciseSet(exercise, "rep", arrayOf(8, 8, 8))
                )
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
        val exerciseSet = wd.exercises[index]

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
        ) {
            Text(exerciseSet.exercise.name, Modifier.fillMaxWidth(0.5f))

            IconButton(onClick = {
                wd.infoExercise = exerciseSet.exercise
            }) {
                Icon(Icons.Default.Info, "Exercise Info")
            }
            Switch(
                checked = wd.lockedExercises.contains(index),
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