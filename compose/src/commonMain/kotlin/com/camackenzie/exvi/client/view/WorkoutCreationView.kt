package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.runtime.saveable.Saver
import com.camackenzie.exvi.core.util.SelfSerializable

object WorkoutCreationView : Viewable {

    @Composable
    override fun View(appState: AppState) {
        ensureActiveAccount(appState)

        val viewData = ViewData(appState, rememberCoroutineScope())
        val workoutData = rememberSaveable(
            saver = WorkoutData.saver(appState.provided)
        ) { WorkoutData(appState.provided) }
        val workoutSearchData = rememberSaveable(saver = WorkoutSearchData.saver()) { WorkoutSearchData() }
        val selectorViewData = rememberSaveable(saver = SelectorViewData.saver()) { SelectorViewData() }

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
                            CancelWorkoutButton(appState)
                        }
                    }
                    Box(
                        Modifier.fillMaxWidth()
                            .fillMaxHeight(0.4f)
                    ) {
                        WorkoutExerciseListView(workoutData, selectorViewData)
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
                            CancelWorkoutButton(appState)
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
                            WorkoutExerciseListView(workoutData, selectorViewData)
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
                RightPaneView.Search.str to {
                    ExerciseSearchView(viewData, workoutData, searchData, selectorData)
                },
                RightPaneView.Info.str to {
                    ExerciseInfoView(workoutData.infoExercise)
                },
                RightPaneView.Generator.str to {
                    WorkoutGeneratorView(viewData, workoutData)
                },
                RightPaneView.Editor.str to {
                    ExerciseSetEditorView(viewData, workoutData)
                },
                RightPaneView.Workout.str to {
                    WorkoutDescriptionEditor(workoutData)
                }
            ),
            currentView = selectorData.rightPane,
            onCurrentViewChange = {
                selectorData.rightPane = it
            }
        )
    }

    @Composable
    private fun WorkoutDescriptionEditor(workoutData: WorkoutData) {
        TextField(
            value = workoutData.description,
            onValueChange = {
                workoutData.description = it
            },
            label = {
                Text("Workout Description")
            },
            placeholder = { Text("Description") }
        )
    }

    @Composable
    private fun WorkoutGeneratorView(
        viewData: ViewData,
        workoutData: WorkoutData
    ) {
        val generatorData = workoutData.generatorData
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
            ) {
                Button(onClick = {
                    viewData.coroutineScope.launch(Dispatchers.Default) {
                        workoutData.exerciseProcessRunning = true
                        viewData.model.exerciseManager.loadStandardExercisesIfEmpty()
                        val generator = WorkoutGenerator(
                            viewData.model.exerciseManager,
                            generatorData.params
                        )
                        val newWorkout = generator.generateWorkout(
                            workoutData.workout,
                            workoutData.lockedExercises.toTypedArray()
                        )
                        workoutData.exercises = newWorkout.exercises.toTypedArray()
                        workoutData.exerciseProcessRunning = false
                    }
                }, enabled = !workoutData.exerciseProcessRunning) {
                    Text("Generate")
                }
                Box {
                    Button(onClick = { generatorData.generatorDropdownExpanded = true }) {
                        Text("Select Generator")
                    }
                    DropdownMenu(expanded = generatorData.generatorDropdownExpanded, onDismissRequest = {
                        generatorData.generatorDropdownExpanded = false
                    }) {
                        fun SetGenerator(generator: Array<ExercisePriorityProvider>) {
                            generatorData.params.providers = generator
                            generatorData.generatorDropdownExpanded = false
                        }

                        DropdownMenuItem(onClick = { SetGenerator(WorkoutGenerator.armPriorities()) }) {
                            Text("Arms")
                        }
                        DropdownMenuItem(onClick = { SetGenerator(WorkoutGenerator.legPriorities()) }) {
                            Text("Legs")
                        }
                        DropdownMenuItem(onClick = { SetGenerator(emptyArray()) }) {
                            Text("Random")
                        }
                        DropdownMenuItem(onClick = { SetGenerator(WorkoutGenerator.corePriorities()) }) {
                            Text("Core")
                        }
                        DropdownMenuItem(onClick = { SetGenerator(WorkoutGenerator.backPriorities()) }) {
                            Text("Back")
                        }
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
            ) {
                OptIntField(
                    modifier = Modifier.fillMaxWidth(0.333f),
                    value = generatorData.minExercises,
                    onValueChange = {
                        generatorData.minExercises = it
                    },
                    maxDigits = 3,
                    label = { Text("Min. Exercises") }
                )
                OptIntField(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    value = generatorData.maxExercises,
                    onValueChange = {
                        generatorData.maxExercises = it
                    },
                    maxDigits = 3,
                    label = { Text("Max. Exercises") }
                )
                OptIntField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if (generatorData.maxExercises == generatorData.minExercises) generatorData.minExercises
                    else null,
                    onValueChange = {
                        generatorData.maxExercises = it
                        generatorData.minExercises = it
                    },
                    placeholder = { Text("N/A") },
                    maxDigits = 3,
                    label = { Text("N. Exercises") }
                )
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
                arrayOf(workoutData.workout),
                coroutineScope = viewData.appState.coroutineScope,
                onFail = {
                    println("Updating workout failed: ${it.toJson()}")
                },
                onSuccess = {
                    println("Workout \"${workoutData.name}\" updated successfully")
                }
            )
            viewData.appState.setView(ExviView.Home)
        }) {
            Text("Finish")
        }
    }

    @Composable
    private fun CancelWorkoutButton(appState: AppState) {
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
                    appState.setView(ExviView.Home)
                }) {
                    Text("Exit")
                }
            }
        }
    }

    @Composable
    private fun ExerciseSetEditorView(
        viewData: ViewData,
        workoutData: WorkoutData,
        modifier: Modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.then(Modifier.verticalScroll(rememberScrollState())),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Null assertions not used because jetpack throws null ptr exception when exercise is removed
            if (workoutData.editorExercise != null) {
                Text(
                    workoutData.editorExercise?.exercise?.name ?: "",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
                TextField(
                    value = workoutData.editorExercise?.unit ?: "",
                    onValueChange = {
                        workoutData.editorExercise = workoutData.editorExercise?.copy(unit = it)
                    },
                    label = { Text("Exercise Set Unit") }
                )

                Text("Sets")
                RepList(
                    exercise = workoutData.editorExercise!!,
                    onValueChange = { it, newReps ->
                        workoutData.editorExercise!!.sets[it].reps = newReps
                        workoutData.editorExercise = workoutData.editorExercise!!.copy()
                    }
                ) { setIdx, repField ->
                    Column {
                        repField()
                        IconButton(onClick = {
                            workoutData.editorExercise = workoutData.editorExercise?.copy(
                                // Remove element at index setIndex
                                sets = workoutData.editorExercise!!.sets.filterIndexed { idx, _ ->
                                    idx != setIdx
                                }.toTypedArray()
                            )
                        }) {
                            Icon(Icons.Default.Delete, "Remove Set")
                        }
                    }
                }
                IconButton(onClick = {
                    workoutData.editorExercise = workoutData.editorExercise?.copy(
                        sets = workoutData.editorExercise!!.sets + SingleExerciseSet(8)
                    )
                }) {
                    Icon(Icons.Default.Add, "Add Set")
                }
            } else {
                Text(
                    "No Exercise Selected for Editing",
                    textAlign = TextAlign.Center
                )
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

                Column(horizontalAlignment = Alignment.Start) {
                    Text("Exercise Type(s): ${exercise.exerciseTypes.toFormattedString()}")
                    Text("Muscles Worked: ${exercise.musclesWorked.map { it.muscle }.toFormattedString()}")
                    Text("Experience Level: ${exercise.experienceLevel}")
                    Text("Force Type: ${exercise.forceType}")
                    Text("Mechanics: ${exercise.mechanics}")
                    if (exercise.hasEquipment()) {
                        Text("Equipment Required: ${exercise.equipment.toFormattedString()}")
                    }
                }

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
        selectorViewData: SelectorViewData,
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
                            AllExercisesListViewItem(workoutData, selectorViewData, searchData.searchExercises[it])
                        }
                    } else {
                        item {
                            LoadingIcon()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WorkoutExerciseListView(
        workoutData: WorkoutData,
        selectorViewData: SelectorViewData,
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
                        WorkoutExerciseListViewItem(workoutData, selectorViewData, it)
                    }
                } else {
                    item {
                        LoadingIcon()
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
        selectorViewData: SelectorViewData,
        exercise: Exercise
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(exercise.name)
            IconButton(onClick = {
                workoutData.infoExercise = exercise
                selectorViewData.rightPane = "Info"
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
        selectorViewData: SelectorViewData,
        index: Int
    ) {
        val exerciseSet = wd.exercises[index]

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
        ) {
            Text(exerciseSet.exercise.name, Modifier.fillMaxWidth(0.5f))

            IconButton(onClick = {
                wd.editorExerciseIndex = index
                selectorViewData.rightPane = RightPaneView.Editor.str
            }) {
                Icon(Icons.Default.Edit, "Edit Exercise")
            }
            IconButton(onClick = {
                wd.infoExercise = exerciseSet.exercise
                selectorViewData.rightPane = RightPaneView.Info.str
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

    private class WorkoutGeneratorData(
        params: WorkoutGeneratorParams,
        generatorDropdownExpanded: Boolean = false,
        minExercises: Int? = params.minExercises,
        maxExercises: Int? = params.maxExercises,
    ) {
        var generatorDropdownExpanded by mutableStateOf(generatorDropdownExpanded)
        var params by mutableStateOf(params)
        var maxExercises by delegatedMutableStateOf(maxExercises, onSet = {
            params.minExercises = it ?: 5
        })
        var minExercises by delegatedMutableStateOf(minExercises, onSet = {
            params.maxExercises = it ?: 8
        })
    }

    private class WorkoutData(
        name: String,
        description: String = "",
        exercises: Array<ExerciseSet> = emptyArray(),
        provided: Workout? = null,
        params: WorkoutGeneratorParams = WorkoutGeneratorParams(providers = generators["Arms"]!!.invoke()),
        lockedExercises: Set<Int> = setOf(),
        exerciseProcessRunning: Boolean = false,
        editorExercise: Int? = null,
        infoExercise: Exercise? = null,
        generatorData: WorkoutGeneratorData = WorkoutGeneratorData(params = params)
    ) {
        var name by mutableStateOf(name)
        var description by mutableStateOf(description)
        var exercises by mutableStateOf(exercises)
        var infoExercise by mutableStateOf(infoExercise)
        var editorExerciseIndex by mutableStateOf(editorExercise)
        var lockedExercises by mutableStateOf(lockedExercises)
        var exerciseProcessRunning by mutableStateOf(exerciseProcessRunning)
        var generatorData by mutableStateOf(generatorData)
        val provided = provided

        var editorExercise: ExerciseSet?
            get() = if (editorExerciseIndex == null) null else exercises[editorExerciseIndex!!]
            set(ex) = if (editorExerciseIndex != null) {
                exercises = exercises.mapIndexed { index, set ->
                    if (index == editorExerciseIndex) ex!! else set
                }.toTypedArray()
            } else throw Exception()

        constructor(provided: Any)
                : this(
            if (provided is Workout) provided.name else "New Workout",
            if (provided is Workout) provided.description else "",
            if (provided is Workout) provided.exercises.toTypedArray() else emptyArray<ExerciseSet>(),
            if (provided is Workout) provided else null
        )

        val workout: Workout
            get() = if (provided is Workout)
                Workout(name, description, arrayListOf(*exercises), provided.id)
            else
                Workout(name, description, arrayListOf(*exercises))

        fun addExercise(ex: ExerciseSet) {
            exercises += ex
        }

        fun removeExercise(index: Int) {
            // Sync with exercise set editor
            if (index == editorExerciseIndex) editorExerciseIndex = null
            // Unlock exercise
            lockExercise(index, false)
            // Shift indexes
            lockedExercises = lockedExercises.map {
                it - ((if (it >= index) 1 else 0) +
                        (if (it >= editorExerciseIndex ?: Int.MAX_VALUE) 1 else 0))
            }.toSet()
            // Remove exercise
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

        companion object {
            @Suppress("UNCHECKED_CAST")
            fun saver(provided: SelfSerializable?): Saver<WorkoutData, Any> = mapSaver(
                save = {
                    mapOf(
                        "name" to it.name,
                        "description" to it.description,
                        "exercises" to it.exercises,
                        "lockedExercises" to it.lockedExercises.toTypedArray(),
                        "editorExerciseIndex" to it.editorExerciseIndex,
                        "infoExercise" to it.infoExercise?.toJson(),
                        "params" to it.generatorData.params.toJson()
                    )
                },
                restore = {
                    val infoExerciseStr = it["infoExercise"] as String?
                    WorkoutData(
                        name = it["name"] as String,
                        description = it["description"] as String,
                        exercises = it["exercises"] as Array<ExerciseSet>,
                        lockedExercises = (it["lockedExercises"] as Array<Int>).toSet(),
                        editorExercise = it["editorExerciseIndex"] as Int?,
                        infoExercise = if (infoExerciseStr == null) null else Json.decodeFromString<Exercise>(
                            infoExerciseStr
                        ),
                        provided = if (provided is Workout) provided else null,
                        generatorData = WorkoutGeneratorData(
                            params = Json.decodeFromString(it["params"] as String)
                        )
                    )
                }
            )
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

        companion object {
            fun saver(): Saver<WorkoutSearchData, Any> = mapSaver(
                save = {
                    mapOf(
                        "searchContent" to it.searchContent
                    )
                }, restore = {
                    WorkoutSearchData(
                        searchContent = it["searchContent"] as String
                    )
                }
            )
        }
    }

    private class SelectorViewData(
        rightPane: String = RightPaneView.Info.str
    ) {
        var rightPane by mutableStateOf(rightPane)

        companion object {
            fun saver(): Saver<SelectorViewData, Any> = mapSaver(
                save = {
                    mapOf(
                        "rightPane" to it.rightPane
                    )
                }, restore = {
                    SelectorViewData(
                        rightPane = it["rightPane"] as String
                    )
                }
            )
        }
    }

    private enum class RightPaneView(val str: String) {
        Info("Info"),
        Search("Search"),
        Generator("Generator"),
        Editor("Editor"),
        Workout("Workout");

        override fun toString(): String = str
    }

}