package com.camackenzie.exvi.client.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.camackenzie.exvi.core.model.*

fun SingleExerciseSet.toComposable() = ComposeSingleExerciseSet(
    reps, weight.copy(), timing.map {
        it.copy()
    }.toTypedArray()
)

fun ExerciseSet.toComposable() = ComposeExerciseSet(exercise, unit, sets.map {
    it.toComposable()
}.toTypedArray())

open class ComposeExerciseSet(
    exercise: Exercise,
    unit: String,
    sets: Array<SingleExerciseSet>,
) : ExerciseSet {
    override val exercise: Exercise by mutableStateOf(exercise)
    override var sets: MutableList<SingleExerciseSet> = mutableStateListOf(*sets)
    override var unit: String by mutableStateOf(unit)

    override fun hashCode(): Int {
        var hash = 17
        hash = hash * 23 + exercise.hashCode()
        hash = hash * 23 + unit.hashCode()
        hash = hash * 23 + sets.hashCode()
        return hash
    }

    override fun getUID(): String = uid
    override fun toJson(): String = toActual().toJson()

    companion object {
        const val uid = "ComposeExerciseSet"
    }
}

open class ComposeSingleExerciseSet(
    reps: Int = 0,
    weight: Mass = 0.kilograms,
    timing: Array<Time>,
) : SingleExerciseSet {

    override var reps: Int by mutableStateOf(reps)
    override var timing: Array<Time> by mutableStateOf(timing)
    override var weight: Mass by mutableStateOf(weight)

    override fun deepValueCopy(): SingleExerciseSet = ComposeSingleExerciseSet(
        reps = reps,
        timing = timing.map { it.copy() }.toTypedArray(),
        weight = weight.copy()
    )

    override fun getUID(): String = uid
    override fun toJson(): String = toActual().toJson()

    companion object {
        const val uid = "ComposeSingleExerciseSet"
    }
}

open class ComposeActiveExercise(
    active: ExerciseSet,
    currentSet: Int,
    target: ExerciseSet,
) : ActiveExercise {
    constructor(other: ActiveExercise) : this(
        other.active.toActual(),
        other.currentSet,
        other.target
    )

    override var active: ExerciseSet by mutableStateOf(active)
    override var currentSet: Int by mutableStateOf(currentSet)
    override val target: ExerciseSet = target

    override fun getUID(): String = uid
    override fun toJson(): String = TODO()

    companion object {
        const val uid = "ComposeActiveExercise"
    }
}