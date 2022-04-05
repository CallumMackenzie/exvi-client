package com.camackenzie.exvi.client.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.EncodedStringCache
import com.camackenzie.exvi.core.util.Identifiable

fun Workout.toComposable() = ComposeWorkout(name, description, exercises, id.copy())

open class ComposeWorkout(
    name: String,
    description: String = "",
    exercises: List<ExerciseSet>,
    id: EncodedStringCache = Identifiable.generateId()
) : Workout {
    constructor(other: Workout?) : this(
        other?.name ?: "New Workout",
        other?.description ?: "",
        other?.exercises ?: emptyList(),
        other?.id ?: Identifiable.generateId()
    )

    override var name: String by mutableStateOf(name)
    override var description: String by mutableStateOf(description)
    override val id: EncodedStringCache = id
    override val exercises: MutableList<ExerciseSet> = mutableStateListOf(*exercises.map {
        it.toComposable()
    }.toTypedArray())

    override fun newActiveWorkout(): ActiveWorkout = ActiveWorkout(this).toComposable()
    override fun toJson(): String = toActual().toJson()
    override fun getUID(): String = uid

    companion object {
        const val uid = "ComposeWorkout"
    }
}

fun ActiveWorkout.toActual() =
    ActualActiveWorkout(name, baseWorkoutId, exercises, activeWorkoutId, startTimeMillis, endTimeMillis)

fun ActiveWorkout.toComposable() =
    ComposeActiveWorkout(name, baseWorkoutId, exercises, activeWorkoutId, startTimeMillis, endTimeMillis)

open class ComposeActiveWorkout(
    name: String,
    baseWorkoutId: EncodedStringCache,
    exercises: Array<ActiveExercise>,
    activeWorkoutId: EncodedStringCache,
    startTimeMillis: Long? = null,
    endTimeMillis: Long? = null,
) : ActiveWorkout {
    constructor(other: ActiveWorkout) : this(
        other.name,
        other.baseWorkoutId,
        other.exercises.map { ComposeActiveExercise(it) }.toTypedArray(),
        other.activeWorkoutId,
        other.startTimeMillis,
        other.endTimeMillis
    )

    override val activeWorkoutId: EncodedStringCache = activeWorkoutId
    override val baseWorkoutId: EncodedStringCache = baseWorkoutId
    override val exercises: Array<ActiveExercise> = exercises
    override val name: String = name
    override var startTimeMillis: Long? by mutableStateOf(startTimeMillis)
    override var endTimeMillis: Long? by mutableStateOf(endTimeMillis)

    override fun getUID(): String = uid
    override fun toJson(): String = ExviSerializer.toJson(this)

    companion object {
        const val uid = "ComposeActiveWorkout"
    }
}