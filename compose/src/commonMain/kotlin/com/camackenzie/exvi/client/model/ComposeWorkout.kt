@file:Suppress("UNCHECKED_CAST")

package com.camackenzie.exvi.client.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.camackenzie.exvi.core.model.*
import com.camackenzie.exvi.core.util.EncodedStringCache
import com.camackenzie.exvi.core.util.Identifiable
import com.camackenzie.exvi.core.util.SelfSerializable
import kotlinx.serialization.KSerializer

open class ComposeWorkout(
    name: String,
    description: String = "",
    exercises: List<ExerciseSet>,
    id: EncodedStringCache = Identifiable.generateId(),
    public: Boolean = false
) : Workout {
    constructor(other: Workout?) : this(
        other?.name ?: "New Workout",
        other?.description ?: "",
        other?.exercises ?: emptyList(),
        other?.id ?: Identifiable.generateId(),
        other?.public ?: false,
    )

    override val serializer: KSerializer<SelfSerializable>
        get() = ActualWorkout.serializer() as KSerializer<SelfSerializable>

    override var name: String by mutableStateOf(name)
    override var description: String by mutableStateOf(description)
    override val id: EncodedStringCache = id
    override val exercises: MutableList<ExerciseSet> = mutableStateListOf(*exercises.map {
        it.toActual()
    }.toTypedArray())
    override var public: Boolean by mutableStateOf(public)

    override fun newActiveWorkout(): ActiveWorkout = ActiveWorkout(this).toComposable()
}

fun ActiveWorkout.toComposable() =
    ComposeActiveWorkout(name, baseWorkoutId, exercises, activeWorkoutId, startTimeMillis, endTimeMillis)

open class ComposeActiveWorkout(
    override val name: String,
    override val baseWorkoutId: EncodedStringCache,
    exercises: Array<ActiveExercise>,
    override val activeWorkoutId: EncodedStringCache,
    startTimeMillis: Long? = null,
    endTimeMillis: Long? = null,
) : ActiveWorkout {
    constructor(other: ActiveWorkout, exercises: Array<ActiveExercise>) : this(
        other.name,
        other.baseWorkoutId,
        exercises,
        other.activeWorkoutId,
        other.startTimeMillis,
        other.endTimeMillis
    )

    constructor(other: ActiveWorkout) : this(
        other,
        other.exercises.map { ComposeActiveExercise(it) }.toTypedArray(),
    )

    override val serializer: KSerializer<SelfSerializable>
        get() = ActualActiveWorkout.serializer() as KSerializer<SelfSerializable>

    override var exercises: Array<ActiveExercise> by mutableStateOf(exercises)
    override var startTimeMillis: Long? by mutableStateOf(startTimeMillis)
    override var endTimeMillis: Long? by mutableStateOf(endTimeMillis)
}