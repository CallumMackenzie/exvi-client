package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.components.*
import com.camackenzie.exvi.client.icons.ExviIcons
import com.camackenzie.exvi.client.model.*
import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.*
import com.soywiz.krypto.SecureRandom
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// TODO: Clean up this code
object WorkoutCreationView : Viewable {

    private const val LOG_TAG = "WORKOUT_CREATION"

    @Composable
    override fun View(appState: AppState) {
        ensureActiveAccount(appState)

        // Local data model for view
        val viewData = ViewData(appState, rememberCoroutineScope())
        val workoutData = rememberSaveable(
            saver = WorkoutData.saver(appState.provided)
        ) {
            WorkoutData(if (appState.provided is Workout) (appState.provided as Workout) else null)
        }
        val workoutSearchData = rememberSaveable(saver = WorkoutSearchData.saver()) { WorkoutSearchData() }
        val selectorViewData = rememberSaveable(saver = SelectorViewData.saver()) { SelectorViewData() }

        // Load standard exercises
        remember {
            viewData.loadingExercises = true
            viewData.coroutineScope.launch {
                appState.model.exerciseManager.loadStandardExercisesIfEmpty()
                // Re-standardize exercises with possible placeholder delegates
                if (workoutData.tryStandardize().isNotEmpty())
                    ExviLogger.i(tag = LOG_TAG) { "Workout standardized" }
                else ExviLogger.i(tag = LOG_TAG) { "Workout already standard" }
                viewData.loadingExercises = false
            }
        }

        // View
        if (viewData.loadingExercises) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingIcon()
            Text("Loading standard exercises")
        } else
            BoxWithConstraints(Modifier.fillMaxSize().padding(10.dp)) {
                if (maxWidth < 740.dp) {
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
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    WorkoutNameField(workoutData, Modifier.fillMaxWidth(0.6f))
                                    FinishWorkoutButton(viewData, workoutData)
                                }
                                CancelWorkoutButton(appState)
                            }
                        }
                        Box(
                            Modifier.fillMaxWidth()
                                .fillMaxHeight(0.4f)
                        ) { WorkoutExerciseListView(workoutData, selectorViewData) }
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
                                WorkoutNameField(workoutData, Modifier.fillMaxWidth(0.6f))
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
                    ExerciseInfoView(workoutData, workoutData.infoExercise)
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
            onCurrentViewChange = { selectorData.rightPane = it },
            coroutineScope = viewData.coroutineScope,
        )
    }

    @Composable
    private fun WorkoutDescriptionEditor(workoutData: WorkoutData) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = workoutData.description,
                onValueChange = { workoutData.description = it },
                label = { Text("Workout Description") },
                placeholder = { Text("Description") }
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Visible to Friends")
                Switch(checked = workoutData.public, onCheckedChange = { workoutData.public = it })
            }
        }
    }

    @Composable
    private fun WorkoutGeneratorView(
        viewData: ViewData,
        workoutData: WorkoutData
    ) {
        val generatorData = workoutData.generatorData
        fun setGenerator(generator: Array<ExercisePriorityProvider>) {
            generatorData.params.providers = generator
            generatorData.generatorDropdownExpanded = false
        }

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
                        // Load standard exercises
                        viewData.model.exerciseManager.loadStandardExercisesIfEmpty()
                        // Create workout generator
                        val generator = WorkoutGenerator(
                            viewData.model.exerciseManager,
                            generatorData.params
                        )
                        // Generate new workout based on this one
                        val newWorkout = generator.generateWorkout(
                            workoutData,
                            workoutData.lockedExercises.toTypedArray()
                        )
                        // Set the exercises in this workout to the generated ones
                        workoutData.exercises.clear()
                        workoutData.exercises.addAll(newWorkout.exercises)
                        workoutData.exerciseProcessRunning = false
                    }
                }, enabled = !workoutData.exerciseProcessRunning) {
                    Text("Generate")
                }
                Box {
                    Button(onClick = { generatorData.generatorDropdownExpanded = true }) {
                        Text("Select Generator")
                    }
                    com.camackenzie.exvi.client.components.DropdownMenu(
                        expanded = generatorData.generatorDropdownExpanded,
                        onDismissRequest = {
                            generatorData.generatorDropdownExpanded = false
                        }) {
                        DropdownMenuItem(onClick = { setGenerator(WorkoutGenerator.armPriorities()) }) {
                            Text("Arms")
                        }
                        DropdownMenuItem(onClick = { setGenerator(WorkoutGenerator.legPriorities()) }) {
                            Text("Legs")
                        }
                        DropdownMenuItem(onClick = { setGenerator(emptyArray()) }) {
                            Text("Random")
                        }
                        DropdownMenuItem(onClick = { setGenerator(WorkoutGenerator.corePriorities()) }) {
                            Text("Core")
                        }
                        DropdownMenuItem(onClick = { setGenerator(WorkoutGenerator.backPriorities()) }) {
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
                    modifier = Modifier.fillMaxWidth(1f / 3f),
                    value = generatorData.minExercises,
                    onValueChange = { generatorData.minExercises = it },
                    maxDigits = 3,
                    label = { Text("Min. Exercises") }
                )
                OptIntField(
                    modifier = Modifier.fillMaxWidth(1f / 2f),
                    value = generatorData.maxExercises,
                    onValueChange = { generatorData.maxExercises = it },
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
                    placeholder = { Text("${generatorData.minExercises ?: "*"}-${generatorData.maxExercises ?: "*"}") },
                    maxDigits = 3,
                    label = { Text("Num. Exercises") }
                )
            }
        }
    }

    @Composable
    private fun WorkoutNameField(workoutData: WorkoutData, modifier: Modifier = Modifier) {
        val regex = Regex("([a-zA-Z0-9.]|\\s)*")
        TextField(modifier = modifier,
            value = workoutData.name,
            label = { Text("Workout Name") },
            placeholder = {
                Text(workoutNamePresets[(SecureRandom.nextDouble() * workoutNamePresets.size).toInt()])
            },
            onValueChange = {
                if (it.length <= 30 && it.matches(regex)) workoutData.name = it
            })
    }

    @Composable
    private fun FinishWorkoutButton(
        viewData: ViewData,
        workoutData: WorkoutData
    ) {
        Button(onClick = {
            val toAdd = workoutData.toActual()
            toAdd.tryStandardize()
            viewData.model.workoutManager?.putWorkouts(
                arrayOf(toAdd),
                coroutineScope = viewData.appState.coroutineScope,
                onFail = {
                    ExviLogger.e(tag = LOG_TAG) { "Updating workout failed with code ${it.statusCode}: ${it.body}" }
                },
                onSuccess = {
                    ExviLogger.i(tag = LOG_TAG) { "Workout \"${workoutData.name}\" updated successfully" }
                }
            ) ?: ExviLogger.w(tag = LOG_TAG) { "Workout manager was null when putting workout" }
            viewData.appState.setView(ExviView.Home)
        }) {
            Text("Finish")
        }
    }

    @Composable
    private fun CancelWorkoutButton(appState: AppState) {
        var promptCancel by rememberSaveable { mutableStateOf(false) }
        if (!promptCancel) {
            Button(onClick = { promptCancel = true }) { Text("Cancel") }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { promptCancel = false }) { Text("Keep Editing") }
                Button(onClick = { appState.setView(ExviView.Home) }) { Text("Exit") }
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
                        // Set unit of editor exercise to "it"
                        workoutData.editorExercise!!.unit = it
                        workoutData.refreshEditorExercise()
                    },
                    label = { Text("Exercise Set Unit") }
                )

                Text("Sets")
                RepList(
                    exercise = workoutData.editorExercise!!,
                    onValueChange = { it, newReps ->
                        // Set reps for set "it" to "newReps"
                        workoutData.editorExercise!!.sets[it].reps = newReps
                        workoutData.refreshEditorExercise()
                    }
                ) { setIdx, repField ->
                    Column {
                        repField()
                        IconButton(onClick = {
                            // Remove element at index index "setIdx"
                            workoutData.editorExercise!!.sets.removeAt(setIdx)
                            workoutData.refreshEditorExercise()
                        }) { Icon(Icons.Default.Delete, "Remove Set") }
                    }
                }
                IconButton(onClick = {
                    // Add new exercise set to editor exercise
                    workoutData.editorExercise!!.sets.add(SingleExerciseSet(8))
                    workoutData.refreshEditorExercise()
                }) {
                    Icon(ExviIcons.Add, "Add Set")
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
        workoutData: WorkoutData,
        exercise: Exercise?
    ) {
        if (exercise != null) {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Row {
                    Text(
                        exercise.name,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                    IconButton(onClick = {
                        workoutData.addExercise(ExerciseSet(exercise, "rep", arrayOf(8, 8, 8)))
                    }) { Icon(ExviIcons.Add, "Add Exercise") }
                }

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

                if (exercise.hasVideoLink()) {
                    Text(
                        "Video", fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                    val created = VideoPlayer(exercise.videoLink, Modifier.fillMaxWidth().requiredHeight(300.dp))
                    if (!created) Text("Video player error")
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
        // Ensure exercises are loaded and sorted
        searchData.ensureExercisesSorted(viewData.model.exerciseManager, viewData.coroutineScope)

        // Search field for exercise keywords
        @Composable
        fun TextSearchField(modifier: Modifier) = TextField(
            modifier = modifier,
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

        @Composable
        fun EquipmentSearchField(modifier: Modifier) = VariantSelector(modifier = modifier,
            variants = viewData.model.exerciseManager.standardEquipment,
            value = searchData.equipment,
            onValueChanged = {
                searchData.equipment = it
                searchData.exercisesSorted = false
            }, content = @Composable {
                Text(it?.name?.lowercase() ?: "any equipment")
            },
            dropdownExpanded = searchData.equipmentDropdownExtended,
            onDropdownExpandedChanged = { searchData.equipmentDropdownExtended = it }
        )

        // Search field for muscles
        @Composable
        fun MuscleSearchField(modifier: Modifier) = VariantSelector(modifier = modifier,
            variants = Muscle.values().sortedWith { a, b -> a.toString().compareTo(b.toString()) }.toTypedArray(),
            value = searchData.muscleWorked,
            onValueChanged = {
                searchData.muscleWorked = it
                searchData.exercisesSorted = false
            },
            content = @Composable { Text(EnumUtils.formatName(it?.name ?: "any muscle")) },
            dropdownExpanded = searchData.muscleDropdownExpanded,
            onDropdownExpandedChanged = { searchData.muscleDropdownExpanded = it }
        )

        // Search field for experience level
        @Composable
        fun ExperienceSearchField(modifier: Modifier) = VariantSelector(modifier = modifier,
            variants = ExerciseExperienceLevel.values().sortedWith { a, b -> a.toString().compareTo(b.toString()) }
                .toTypedArray(),
            value = searchData.experienceLevel,
            onValueChanged = {
                searchData.experienceLevel = it
                searchData.exercisesSorted = false
            },
            content = @Composable { Text(EnumUtils.formatName(it?.toString() ?: "any experience")) },
            dropdownExpanded = searchData.experienceLevelDropdownExtended,
            onDropdownExpandedChanged = { searchData.experienceLevelDropdownExtended = it }
        )

        // Search field for mechanics
        @Composable
        fun MechanicsSearchField(modifier: Modifier) = VariantSelector(modifier = modifier,
            variants = ExerciseMechanics.values().sortedWith { a, b -> a.toString().compareTo(b.toString()) }
                .toTypedArray(),
            value = searchData.mechanics,
            onValueChanged = {
                searchData.mechanics = it
                searchData.exercisesSorted = false
            },
            content = @Composable { Text(EnumUtils.formatName(it?.toString() ?: "any mechanics")) },
            dropdownExpanded = searchData.mechanicsDropdownExtended,
            onDropdownExpandedChanged = { searchData.mechanicsDropdownExtended = it }
        )

        Column(
            listViewModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            BoxWithConstraints {
                if (maxWidth > 900.dp) Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                ) {
                    // Exercise name search input
                    TextSearchField(Modifier.fillMaxWidth(1f / 5f))
                    MuscleSearchField(Modifier.fillMaxWidth(1f / 4f))
                    ExperienceSearchField(Modifier.fillMaxWidth(1f / 3f))
                    MechanicsSearchField(Modifier.fillMaxWidth(1f / 2f))
                    EquipmentSearchField(Modifier.fillMaxWidth())
                }
                else if (maxWidth > 650.dp) Column(
                    Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        TextSearchField(Modifier.fillMaxWidth(1f / 2f))
                        MuscleSearchField(Modifier.fillMaxWidth())
                    }
                    Row(Modifier.fillMaxWidth()) {
                        ExperienceSearchField(Modifier.fillMaxWidth(1f / 3f))
                        MechanicsSearchField(Modifier.fillMaxWidth(1f / 2f))
                        EquipmentSearchField(Modifier.fillMaxWidth())
                    }
                }
                else if (maxWidth > 500.dp && maxHeight > 250.dp) Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    ) {
                        TextSearchField(Modifier.fillMaxWidth(0.6f))
                        MuscleSearchField(Modifier.fillMaxWidth())
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    ) {
                        ExperienceSearchField(Modifier.fillMaxWidth(1f / 3f))
                        MechanicsSearchField(Modifier.fillMaxWidth(1f / 2f))
                        EquipmentSearchField(Modifier.fillMaxWidth())
                    }
                }
                else if (maxWidth > 400.dp && maxHeight > 350.dp) Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
                ) {
                    TextSearchField(Modifier.fillMaxWidth())
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    ) {
                        MuscleSearchField(Modifier.fillMaxWidth(1f / 2f))
                        ExperienceSearchField(Modifier.fillMaxWidth())
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    ) {
                        MechanicsSearchField(Modifier.fillMaxWidth(1f / 2f))
                        EquipmentSearchField(Modifier.fillMaxWidth())
                    }
                }
                else Expandable(header = @Composable { Text("Filters") }) {
                    Column(
                        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
                    ) {
                        TextSearchField(Modifier.fillMaxWidth())
                        MuscleSearchField(Modifier.fillMaxWidth())
                        ExperienceSearchField(Modifier.fillMaxWidth())
                        MechanicsSearchField(Modifier.fillMaxWidth())
                        EquipmentSearchField(Modifier.fillMaxWidth())
                    }
                }
            }
            ExviBox {
                LazyColumn(
                    listViewModifier,
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!searchData.processRunning) items(searchData.searchExercises.size) {
                        AllExercisesListViewItem(workoutData, selectorViewData, searchData.searchExercises[it])
                    }
                    else item { LoadingIcon() }
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
                if (!workoutData.exerciseProcessRunning) items(workoutData.exercises.size) {
                    WorkoutExerciseListViewItem(workoutData, selectorViewData, it)
                } else item { LoadingIcon() }
                if (workoutData.exercises.isEmpty()
                    && !workoutData.exerciseProcessRunning
                ) item { Text("There are no exercises in this workout") }
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
            Text(exercise.name, modifier = Modifier.fillMaxWidth(0.6f), overflow = TextOverflow.Ellipsis)
            IconButton(onClick = {
                workoutData.infoExercise = exercise
                selectorViewData.rightPane = "Info"
            }) { Icon(Icons.Default.Info, "Exercise Info") }
            IconButton(onClick = {
                workoutData.addExercise(ExerciseSet(exercise, "rep", arrayOf(8, 8, 8)))
            }) { Icon(ExviIcons.Add, "Add Exercise") }
        }
    }

    @Composable
    private fun WorkoutExerciseListViewItem(
        wd: WorkoutData,
        selectorViewData: SelectorViewData,
        index: Int
    ) {
        val exerciseSet = wd.exercises[index]
        val moveUp: () -> Unit = {
            if (index > 0)
                wd.swapExercises(index, index - 1)
        }
        val moveDown: () -> Unit = {
            if (index + 1 < wd.exercises.size)
                wd.swapExercises(index, index + 1)
        }
        val setEditing: () -> Unit = {
            wd.editorExerciseIndex = index
            selectorViewData.rightPane = RightPaneView.Editor.str
        }
        val viewInfo: () -> Unit = {
            wd.infoExercise = exerciseSet.exercise
            selectorViewData.rightPane = RightPaneView.Info.str
        }
        val toggleLock: (Boolean) -> Unit = { wd.lockExercise(index, it) }
        val removeSelf: () -> Unit = { wd.removeExercise(index) }

        BoxWithConstraints {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
            ) {
                Text(exerciseSet.exercise.name, Modifier.fillMaxWidth(0.4f), overflow = TextOverflow.Ellipsis)
                if (this@BoxWithConstraints.maxWidth > 560.dp) {
                    // Large displays
                    IconButton(onClick = moveUp) { Icon(ExviIcons.ArrowUp, "Move Up") }
                    IconButton(onClick = moveDown) { Icon(ExviIcons.ArrowDown, "Move Down") }
                    IconButton(onClick = setEditing) { Icon(Icons.Default.Edit, "Edit Exercise") }
                    IconButton(onClick = viewInfo) { Icon(Icons.Default.Info, "Exercise Info") }
                    Switch(checked = wd.lockedExercises.contains(index), onCheckedChange = toggleLock)
                    IconButton(onClick = removeSelf) { Icon(Icons.Default.Close, "Remove Exercise") }
                } else if (this@BoxWithConstraints.maxWidth > 375.dp) {
                    // Medium displays
                    var dropdownExtended by remember { mutableStateOf(false) }
                    IconButton(onClick = moveUp) { Icon(ExviIcons.ArrowUp, "Move Up") }
                    IconButton(onClick = moveDown) { Icon(ExviIcons.ArrowDown, "Move Down") }
                    Switch(checked = wd.lockedExercises.contains(index), onCheckedChange = toggleLock)
                    Box {
                        IconButton(onClick = { dropdownExtended = true }) {
                            Icon(Icons.Default.Menu, "More Controls")
                        }
                        com.camackenzie.exvi.client.components.DropdownMenu(
                            expanded = dropdownExtended,
                            onDismissRequest = {
                                dropdownExtended = false
                            }) {
                            DropdownMenuItem(onClick = viewInfo) {
                                Icon(Icons.Default.Info, "Exercise Info")
                            }
                            DropdownMenuItem(onClick = setEditing) {
                                Icon(Icons.Default.Edit, "Edit Exercise")
                            }
                            DropdownMenuItem(onClick = {
                                removeSelf()
                                dropdownExtended = false
                            }) {
                                Icon(Icons.Default.Close, "Remove Exercise")
                            }
                        }
                    }
                } else if (this@BoxWithConstraints.maxWidth > 220.dp) {
                    // Small displays
                    var dropdownExtended by remember { mutableStateOf(false) }
                    IconButton(onClick = moveUp) { Icon(ExviIcons.ArrowUp, "Move Up") }
                    IconButton(onClick = moveDown) { Icon(ExviIcons.ArrowDown, "Move Down") }
                    Box {
                        IconButton(onClick = { dropdownExtended = true }) {
                            Icon(Icons.Default.Menu, "More Controls")
                        }
                        com.camackenzie.exvi.client.components.DropdownMenu(
                            expanded = dropdownExtended,
                            onDismissRequest = {
                                dropdownExtended = false
                            }) {
                            Row(Modifier.padding(2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Lock")
                                Switch(checked = wd.lockedExercises.contains(index), onCheckedChange = toggleLock)
                            }
                            DropdownMenuItem(onClick = viewInfo) {
                                Icon(Icons.Default.Info, "Exercise Info")
                            }
                            DropdownMenuItem(onClick = setEditing) {
                                Icon(Icons.Default.Edit, "Edit Exercise")
                            }
                            DropdownMenuItem(onClick = {
                                removeSelf()
                                dropdownExtended = false
                            }) {
                                Icon(Icons.Default.Close, "Remove Exercise")
                            }
                        }
                    }
                } else {
                    // For ultra tiny displays
                    var dropdownExtended by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { dropdownExtended = true }) {
                            Icon(Icons.Default.Menu, "Controls")
                        }
                        com.camackenzie.exvi.client.components.DropdownMenu(
                            expanded = dropdownExtended,
                            onDismissRequest = {
                                dropdownExtended = false
                            }) {
                            DropdownMenuItem(onClick = moveUp) {
                                Icon(ExviIcons.ArrowUp, "Move Up")
                            }
                            DropdownMenuItem(onClick = moveDown) {
                                Icon(ExviIcons.ArrowDown, "Move Down")
                            }
                            Row(Modifier.padding(2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Lock")
                                Switch(checked = wd.lockedExercises.contains(index), onCheckedChange = toggleLock)
                            }
                            DropdownMenuItem(onClick = viewInfo) {
                                Icon(Icons.Default.Info, "Exercise Info")
                            }
                            DropdownMenuItem(onClick = setEditing) {
                                Icon(Icons.Default.Edit, "Edit Exercise")
                            }
                            DropdownMenuItem(onClick = {
                                removeSelf()
                                dropdownExtended = false
                            }) {
                                Icon(Icons.Default.Close, "Remove Exercise")
                            }
                        }
                    }
                }
            }
        }
    }

    private val workoutNamePresets = arrayOf(
        "Pull Day", "Push Day", "Leg Day", "Chest Day",
        "Bicep Bonanza", "Quad Isolation", "Calf Cruncher",
        "Forearm Fiesta", "The Quadfather", "Quadzilla",
        "Shoulders", "Back Builder", "Core", "Cardio Day 1",
        "Deltoid Destroyer", "Shoulder Shredder", "Core Killer",
        "More Core", "Roko's Rhomboids", "Jacked Jake"
    )

    private val generators = mapOf(
        "Random" to { emptyArray() },
        "Arms" to WorkoutGenerator::armPriorities,
        "Legs" to WorkoutGenerator::legPriorities
    )

    private data class ViewData(
        val appState: AppState,
        val coroutineScope: CoroutineScope,
    ) {
        var loadingExercises by mutableStateOf(false)

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
        description: String,
        exercises: List<ExerciseSet>,
        id: EncodedStringCache,
        params: WorkoutGeneratorParams = WorkoutGeneratorParams(providers = generators["Arms"]!!.invoke()),
        lockedExercises: Set<Int> = setOf(),
        exerciseProcessRunning: Boolean = false,
        editorExercise: Int? = null,
        infoExercise: Exercise? = null,
        generatorData: WorkoutGeneratorData = WorkoutGeneratorData(params = params),
        public: Boolean = false
    ) : ComposeWorkout(name, description, exercises, id, public) {
        constructor(base: Workout?) : this(
            base?.name ?: "New Workout",
            base?.description ?: "",
            base?.exercises ?: emptyList(),
            base?.id ?: Identifiable.generateId(),
            public = base?.public ?: false
        )

        var infoExercise by mutableStateOf(infoExercise)
        var editorExerciseIndex by mutableStateOf(editorExercise)
        var lockedExercises by mutableStateOf(lockedExercises)
        var exerciseProcessRunning by mutableStateOf(exerciseProcessRunning)
        var generatorData by mutableStateOf(generatorData)

        var editorExercise: ExerciseSet?
            get() = if (editorExerciseIndex == null) null else exercises[editorExerciseIndex!!]
            set(ex) = if (ex == null) throw Exception("Exercise was null")
            else if (editorExerciseIndex == null) {
                editorExerciseIndex = exercises.indexOf(ex)
            } else {
                exercises[editorExerciseIndex!!] = ex
            }

        // Reload editor exercise for full recomposition
        fun refreshEditorExercise() {
            if (editorExercise != null)
                editorExercise = editorExercise!!.toComposable()
        }

        // Append exercise to end of list
        fun addExercise(ex: ExerciseSet) {
            exercises.add(ex)
        }

        // Swap exercises at index a and b
        fun swapExercises(a: Int, b: Int) {
            // If indexes equal, don't swap
            if (a == b) return

            // Switch actual exercises
            val aVal = exercises[a]
            exercises[a] = exercises[b]
            exercises[b] = aVal

            // Sync exercise locking
            val aLocked = lockedExercises.contains(a)
            val bLocked = lockedExercises.contains(b)
            lockExercise(a, bLocked)
            lockExercise(b, aLocked)

            // Sync exercise editor to new index if needed
            if (a == editorExerciseIndex) editorExerciseIndex = b
            else if (b == editorExerciseIndex) editorExerciseIndex = a
        }

        // Remove exercise at index
        fun removeExercise(index: Int) {
            // Sync with exercise set editor
            if (index == editorExerciseIndex) editorExerciseIndex = null
            else if (editorExerciseIndex != null && index < editorExerciseIndex!!) editorExerciseIndex =
                editorExerciseIndex!!.dec()
            // Unlock exercise
            lockExercise(index, false)
            // Shift locked exercise indexes
            lockedExercises = lockedExercises.map {
                it - ((if (it >= index) 1 else 0) +
                        (if (it >= (editorExerciseIndex ?: Int.MAX_VALUE)) 1 else 0))
            }.toSet()
            // Remove exercise
            exercises.removeAt(index)
        }

        // Lock or unlock exercise at index
        fun lockExercise(index: Int, lock: Boolean) {
            lockedExercises = if (lock) // Lock exercise
                setOf(*lockedExercises.toTypedArray(), index)
            else // Unlock exercise
                lockedExercises.filter {
                    it != index
                }.toSet()
        }

        companion object {
            @Suppress("UNCHECKED_CAST")
            fun saver(provided: SelfSerializable?): Saver<WorkoutData, Any> = mapSaver(
                save = {
                    mapOf(
                        "name" to it.name,
                        "description" to it.description,
                        "exercises" to ExviSerializer.toJson(it.exercises.toTypedArray()),
                        "id" to it.id.getEncoded(),
                        "lockedExercises" to ExviSerializer.toJson(it.lockedExercises.toTypedArray()),
                        "editorExerciseIndex" to it.editorExerciseIndex,
                        "infoExercise" to if (it.infoExercise == null) null else ExviSerializer.toJson(it.infoExercise!!),
                        "params" to it.generatorData.params.toJson(),
                        "public" to it.public,
                    )
                },
                restore = {
                    val infoExerciseStr = it["infoExercise"] as String?
                    WorkoutData(
                        name = it["name"] as String,
                        description = it["description"] as String,
                        exercises = listOf(*ExviSerializer.fromJson<Array<ExerciseSet>>(it["exercises"] as String)),
                        id = EncodedStringCache.fromEncoded(it["id"] as String),
                        lockedExercises = (ExviSerializer.fromJson<Array<Int>>(it["lockedExercises"] as String)).toSet(),
                        editorExercise = it["editorExerciseIndex"] as Int?,
                        infoExercise = if (infoExerciseStr == null) null else ExviSerializer.fromJson<Exercise>(
                            infoExerciseStr
                        ),
                        generatorData = WorkoutGeneratorData(
                            params = ExviSerializer.fromJson(it["params"] as String)
                        ),
                        public = it["public"] as Boolean
                    )
                }
            )
        }
    }

    private class WorkoutSearchData(
        searchContent: String = "",
        exercisesSorted: Boolean = false,
        searchExercises: Array<Exercise> = emptyArray(),
        processRunning: Boolean = false,
        muscleWorked: Muscle? = null,
        experienceLevel: ExerciseExperienceLevel? = null,
        mechanics: ExerciseMechanics? = null,
        equipment: ExerciseEquipment? = null
    ) {
        var exercisesSorted by mutableStateOf(exercisesSorted)
        var searchExercises by mutableStateOf(searchExercises)
        var processRunning by mutableStateOf(processRunning)

        var searchContent by mutableStateOf(searchContent)

        var muscleWorked by mutableStateOf(muscleWorked)
        var muscleDropdownExpanded by mutableStateOf(false)

        var experienceLevel by mutableStateOf(experienceLevel)
        var experienceLevelDropdownExtended by mutableStateOf(false)

        var mechanics by mutableStateOf(mechanics)
        var mechanicsDropdownExtended by mutableStateOf(false)

        var equipment by mutableStateOf(equipment)
        var equipmentDropdownExtended by mutableStateOf(false)

        var searchJob: Job? = null

        fun ensureExercisesSorted(exerciseManager: ExerciseManager, coroutineScope: CoroutineScope) {
            // Ensure exercises are loaded into memory
            if (!processRunning && searchExercises.isEmpty() && !exercisesSorted) {
                coroutineScope.launch(Dispatchers.Default) {
                    processRunning = true
                    exerciseManager.loadStandardExercisesIfEmpty()
                    searchExercises = exerciseManager.exercises.toTypedArray()
                    exercisesSorted = false
                    processRunning = false
                }
            }

            // Sort exercises if they need to be sorted, and they are not already being sorted
            if (!processRunning
                && searchExercises.isNotEmpty()
                && !exercisesSorted
            ) {
                val oldJob = searchJob
                searchJob = coroutineScope.launch(Dispatchers.Default) {
                    oldJob?.cancelAndJoin()
                    processRunning = true

                    val searchKeywords = searchContent.split("\\s+")
                    searchExercises.sortBy {
                        var sum = Int.MAX_VALUE
                        for (word in searchKeywords)
                            if (it.name.contains(word, true)) sum -= 1
                        if (it.mechanics == mechanics) sum -= 1
                        if (it.experienceLevel == experienceLevel) sum -= 1
                        if (muscleWorked != null && exerciseManager.exercisesByMuscle[muscleWorked]?.contains(it) ?: false)
                            sum -= 1
                        if (it.equipment.contains(equipment)) sum -= 1
                        sum
                    }

                    processRunning = false
                    exercisesSorted = true
                }
            }
        }

        companion object {
            fun saver(): Saver<WorkoutSearchData, Any> = mapSaver(
                save = {
                    mapOf(
                        "searchContent" to it.searchContent,
                        "muscleWorked" to it.muscleWorked,
                        "experienceLevel" to it.experienceLevel,
                        "mechanics" to it.mechanics,
                        "equipment" to it.equipment?.name,
                    )
                }, restore = {
                    WorkoutSearchData(
                        searchContent = it["searchContent"] as String,
                        muscleWorked = it["muscleWorked"] as Muscle?,
                        experienceLevel = it["experienceLevel"] as ExerciseExperienceLevel?,
                        mechanics = it["mechanics"] as ExerciseMechanics?,
                        equipment = if (it["equipment"] == null) null else
                            ExerciseEquipment(it["equipment"] as String),
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